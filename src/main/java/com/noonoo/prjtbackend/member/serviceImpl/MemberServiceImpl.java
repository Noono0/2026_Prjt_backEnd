package com.noonoo.prjtbackend.member.serviceImpl;

import com.noonoo.prjtbackend.common.config.RequestContext;
import com.noonoo.prjtbackend.common.paging.PageResponse;
import com.noonoo.prjtbackend.common.paging.PagingUtils;
import com.noonoo.prjtbackend.member.dto.MemberDto;
import com.noonoo.prjtbackend.member.dto.MemberSaveRequest;
import com.noonoo.prjtbackend.member.dto.MemberSearchCondition;
import com.noonoo.prjtbackend.member.mapper.MemberMapper;
import com.noonoo.prjtbackend.member.mapper.MemberRoleMapper;
import com.noonoo.prjtbackend.member.service.MemberService;
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
    private final PasswordEncoder passwordEncoder;

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

        int n = memberMapper.insertMember(condition);
        Long seq = condition.getMemberSeq();
        if (seq == null) {
            log.warn("insertMember 후 memberSeq 없음 — MEMBER_ROLE 생략");
            return n;
        }

        List<String> roles = normalizeRoleCodes(condition.getRoleCodes());
        replaceMemberRoles(seq, roles, condition.getCreateId(), condition.getCreateIp());
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

        int n = memberMapper.updateMember(condition);

        if (condition.getRoleCodes() != null && condition.getMemberSeq() != null) {
            List<String> roles = normalizeRoleCodes(condition.getRoleCodes());
            replaceMemberRoles(condition.getMemberSeq(), roles, condition.getModifyId(), condition.getModifyIp());
        }

        return n;
    }

    @Override
    @Transactional
    public int deleteMember(Long memberSeq) {
        memberRoleMapper.deleteByMemberSeq(memberSeq);
        return memberMapper.deleteMember(memberSeq);
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
}
