package com.noonoo.prjtbackend.member.serviceImpl;

import com.noonoo.prjtbackend.common.config.RequestContext;
import com.noonoo.prjtbackend.common.paging.PageResponse;
import com.noonoo.prjtbackend.common.paging.PagingUtils;
import com.noonoo.prjtbackend.member.dto.MemberDto;
import com.noonoo.prjtbackend.member.dto.MemberSaveRequest;
import com.noonoo.prjtbackend.member.dto.MemberSearchCondition;
import com.noonoo.prjtbackend.member.mapper.MemberMapper;
import com.noonoo.prjtbackend.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public PageResponse<MemberDto> findMembers(MemberSearchCondition condition) {
        long totalCount = memberMapper.findMembersCnt(condition);
        List<MemberDto> items = memberMapper.findMembers(condition);
        return PagingUtils.toPageResponse(condition, totalCount, items);
    }

    @Override
    public MemberDto findMemberDetail(Long memberSeq) {
        return memberMapper.findMemberById(memberSeq);
    }

    @Override
    public MemberDto findIdCheck(MemberSaveRequest condition) {
        return memberMapper.findIdCheck(condition);
    }

    @Override
    public int createMember(MemberSaveRequest condition) {
        log.info("=======> /api/members/createMember serviceimpl param={}", condition);

        String loginMemberId = RequestContext.getLoginMemberId();
        String clientIp = RequestContext.getClientIp();

        condition.setCreateId(StringUtils.hasText(loginMemberId) ? loginMemberId : "SYSTEM");
        condition.setModifyId(StringUtils.hasText(loginMemberId) ? loginMemberId : "SYSTEM");
        condition.setCreateIp(clientIp);
        condition.setModifyIp(clientIp);

        if (StringUtils.hasText(condition.getMemberPwd())) {
            condition.setMemberPwd(passwordEncoder.encode(condition.getMemberPwd()));
        }

        return memberMapper.insertMember(condition);
    }

    @Override
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

        return memberMapper.updateMember(condition);
    }

    @Override
    public int deleteMember(Long memberSeq) {
        return memberMapper.deleteMember(memberSeq);
    }
}