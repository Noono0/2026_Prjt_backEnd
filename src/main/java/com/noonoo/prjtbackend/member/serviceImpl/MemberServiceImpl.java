package com.noonoo.prjtbackend.member.serviceImpl;

import com.noonoo.prjtbackend.common.config.RequestContext;
import com.noonoo.prjtbackend.common.paging.PageResponse;
import com.noonoo.prjtbackend.common.paging.PagingUtils;
import com.noonoo.prjtbackend.member.dto.MemberDto;
import com.noonoo.prjtbackend.member.dto.MemberStreamerProfileDto;
import com.noonoo.prjtbackend.member.dto.MemberStreamerProfileSaveRequest;
import com.noonoo.prjtbackend.member.dto.PasswordChangeRequest;
import com.noonoo.prjtbackend.member.dto.MemberSaveRequest;
import com.noonoo.prjtbackend.member.dto.MemberSearchCondition;
import com.noonoo.prjtbackend.file.mapper.AttachFileMapper;
import com.noonoo.prjtbackend.member.mapper.MemberMapper;
import com.noonoo.prjtbackend.member.mapper.MemberRoleMapper;
import com.noonoo.prjtbackend.member.mapper.MemberStreamerProfileMapper;
import com.noonoo.prjtbackend.member.service.MemberService;
import com.noonoo.prjtbackend.member.service.WalletPointGrantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;
    private final MemberRoleMapper memberRoleMapper;
    private final MemberStreamerProfileMapper memberStreamerProfileMapper;
    private final AttachFileMapper attachFileMapper;
    private final PasswordEncoder passwordEncoder;
    private final WalletPointGrantService walletPointGrantService;

    @Override
    public PageResponse<MemberDto> findMembers(MemberSearchCondition condition) {
        long totalCount = memberMapper.findMembersCnt(condition);
        List<MemberDto> items = memberMapper.findMembers(condition);
        return PagingUtils.toPageResponse(condition, totalCount, items);
    }

    @Override
    public MemberDto findMemberDetail(Long memberSeq) {
        MemberDto m = memberMapper.findMemberById(memberSeq);
        if (m != null) {
            List<String> codes = memberRoleMapper.findRoleCodesByMemberSeq(memberSeq);
            m.setRoleCodes(codes != null ? codes : List.of());
            MemberStreamerProfileDto profile = memberStreamerProfileMapper.selectByMemberSeq(memberSeq);
            m.setStreamerProfile(profile);
        }
        return m;
    }

    @Override
    public MemberDto findIdCheck(MemberSaveRequest condition) {
        return memberMapper.findIdCheck(condition);
    }

    @Override
    @Transactional
    public int createMember(MemberSaveRequest condition) {
        log.info("=======> /api/members/createMember serviceimpl param={}", condition);

        String loginMemberId = RequestContext.getLoginMemberId();
        String clientIp = RequestContext.getClientIp();

        condition.setCreateId(StringUtils.hasText(loginMemberId) ? loginMemberId : "SYSTEM");
        condition.setModifyId(StringUtils.hasText(loginMemberId) ? loginMemberId : "SYSTEM");
        condition.setCreateIp(clientIp);
        condition.setModifyIp(clientIp);

        if (!StringUtils.hasText(condition.getGradeCode())) {
            condition.setGradeCode("NORMAL");
        }
        if (!StringUtils.hasText(condition.getStatusCode())) {
            condition.setStatusCode("ACTIVE");
        }

        if (StringUtils.hasText(condition.getMemberPwd())) {
            condition.setMemberPwd(passwordEncoder.encode(condition.getMemberPwd()));
        }

        prepareProfileImage(condition);

        int n = memberMapper.insertMember(condition);
        Long seq = condition.getMemberSeq();
        if (seq == null) {
            log.warn("insertMember 후 memberSeq 없음 — MEMBER_ROLE 생략");
            return n;
        }

        List<String> roles = normalizeRoleCodes(condition.getRoleCodes());
        replaceMemberRoles(seq, roles, condition.getCreateId(), condition.getCreateIp());
        upsertStreamerProfileAfterMemberSave(seq, condition.getStreamerProfile(), true);
        try {
            walletPointGrantService.grantSignup(seq);
        } catch (Exception e) {
            log.warn("회원가입 포인트 지급 실패 memberSeq={}: {}", seq, e.toString());
        }
        return n;
    }

    @Override
    @Transactional
    public int updateMember(MemberSaveRequest condition) {
        String loginMemberId = RequestContext.getLoginMemberId();
        String clientIp = RequestContext.getClientIp();

        condition.setModifyId(StringUtils.hasText(loginMemberId) ? loginMemberId : "SYSTEM");
        condition.setModifyIp(clientIp);

        if (StringUtils.hasText(condition.getMemberPwd())) {
            condition.setMemberPwd(passwordEncoder.encode(condition.getMemberPwd()));
        } else {
            condition.setMemberPwd(null);
        }

        prepareProfileImage(condition);

        int n = memberMapper.updateMember(condition);

        if (condition.getRoleCodes() != null && condition.getMemberSeq() != null) {
            List<String> roles = normalizeRoleCodes(condition.getRoleCodes());
            replaceMemberRoles(condition.getMemberSeq(), roles, condition.getModifyId(), condition.getModifyIp());
        }

        upsertStreamerProfileAfterMemberSave(condition.getMemberSeq(), condition.getStreamerProfile(), false);
        return n;
    }

    @Override
    @Transactional
    public int changeMyPassword(PasswordChangeRequest request) {
        Long loginMemberSeq = RequestContext.getLoginMemberSeq();
        if (loginMemberSeq == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }
        if (!StringUtils.hasText(request.getCurrentPassword()) || !StringUtils.hasText(request.getNewPassword())) {
            throw new RuntimeException("현재 비밀번호와 새 비밀번호를 입력해주세요.");
        }

        MemberDto member = memberMapper.findMemberById(loginMemberSeq);
        if (member == null) {
            throw new RuntimeException("회원 정보를 찾을 수 없습니다.");
        }
        if (!passwordEncoder.matches(request.getCurrentPassword(), member.getMemberPwd())) {
            throw new RuntimeException("현재 비밀번호가 일치하지 않습니다.");
        }

        String loginMemberId = RequestContext.getLoginMemberId();
        String clientIp = RequestContext.getClientIp();
        return memberMapper.updateMemberPassword(
                loginMemberSeq,
                passwordEncoder.encode(request.getNewPassword()),
                StringUtils.hasText(loginMemberId) ? loginMemberId : "SYSTEM",
                clientIp
        );
    }

    @Override
    @Transactional
    public int deleteMember(Long memberSeq) {
        memberRoleMapper.deleteByMemberSeq(memberSeq);
        return memberMapper.deleteMember(memberSeq);
    }

    /**
     * 프로필 이미지는 attach_file 에만 저장(file_seq). base64(data:) 저장은 거부.
     * file_seq 가 있으면 PROFILE_IMAGE_URL 은 비움(레거시 혼입 방지).
     */
    private void prepareProfileImage(MemberSaveRequest req) {
        Long fileSeq = req.getProfileImageFileSeq();
        if (fileSeq != null) {
            if (attachFileMapper.selectByFileSeq(fileSeq) == null) {
                throw new IllegalArgumentException("프로필 이미지 파일을 찾을 수 없습니다. 다시 업로드해 주세요.");
            }
            req.setProfileImageUrl(null);
            return;
        }
        String url = req.getProfileImageUrl();
        if (url != null && url.startsWith("data:")) {
            throw new IllegalArgumentException("프로필 이미지는 base64로 저장할 수 없습니다. 이미지 업로드 후 저장해 주세요.");
        }
    }

    private static List<String> normalizeRoleCodes(List<String> input) {
        Set<String> set = new LinkedHashSet<>();
        if (input != null) {
            for (String c : input) {
                if (StringUtils.hasText(c)) {
                    set.add(c.trim());
                }
            }
        }
        if (set.isEmpty()) {
            set.add("USER");
        }
        return new ArrayList<>(set);
    }

    private void replaceMemberRoles(Long memberSeq, List<String> roleCodes, String crtId, String crtIp) {
        memberRoleMapper.deleteByMemberSeq(memberSeq);
        for (String code : roleCodes) {
            memberRoleMapper.insertMemberRole(memberSeq, code, crtId, crtIp);
        }
    }

    /**
     * 신규: 프로필 있으면 저장. 수정: 요청에 streamerProfile 키가 없으면(null) 기존 유지, 있으면 내용 반영(전부 비우면 행 삭제).
     */
    private void upsertStreamerProfileAfterMemberSave(Long memberSeq, MemberStreamerProfileSaveRequest raw, boolean isCreate) {
        if (memberSeq == null) {
            return;
        }
        if (raw == null && !isCreate) {
            return;
        }
        MemberStreamerProfileSaveRequest p = normalizeStreamerProfile(raw);
        if (isCreate) {
            if (p == null || isStreamerProfileEmpty(p)) {
                return;
            }
            memberStreamerProfileMapper.insertProfile(memberSeq, p);
            return;
        }
        if (raw == null) {
            return;
        }
        if (p == null || isStreamerProfileEmpty(p)) {
            memberStreamerProfileMapper.deleteByMemberSeq(memberSeq);
            return;
        }
        if (memberStreamerProfileMapper.countByMemberSeq(memberSeq) > 0) {
            memberStreamerProfileMapper.updateProfile(memberSeq, p);
        } else {
            memberStreamerProfileMapper.insertProfile(memberSeq, p);
        }
    }

    private static MemberStreamerProfileSaveRequest normalizeStreamerProfile(MemberStreamerProfileSaveRequest in) {
        if (in == null) {
            return null;
        }
        MemberStreamerProfileSaveRequest o = new MemberStreamerProfileSaveRequest();
        o.setInstagramUrl(trimToNull(in.getInstagramUrl()));
        o.setYoutubeUrl(trimToNull(in.getYoutubeUrl()));
        o.setSoopChannelUrl(trimToNull(in.getSoopChannelUrl()));
        o.setCompanyCategoryCode(trimToNull(in.getCompanyCategoryCode()));
        o.setBloodType(trimToNull(in.getBloodType()));
        o.setCareerHistory(trimToNull(in.getCareerHistory()));
        return o;
    }

    private static String trimToNull(String s) {
        if (!StringUtils.hasText(s)) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static boolean isStreamerProfileEmpty(MemberStreamerProfileSaveRequest p) {
        if (p == null) {
            return true;
        }
        return !StringUtils.hasText(p.getInstagramUrl())
                && !StringUtils.hasText(p.getYoutubeUrl())
                && !StringUtils.hasText(p.getSoopChannelUrl())
                && !StringUtils.hasText(p.getCompanyCategoryCode())
                && !StringUtils.hasText(p.getBloodType())
                && !StringUtils.hasText(p.getCareerHistory());
    }
}
