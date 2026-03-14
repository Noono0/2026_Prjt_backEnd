package com.noonoo.prjtbackend.member.serviceImpl;

import com.noonoo.prjtbackend.common.config.RequestContext;
import com.noonoo.prjtbackend.member.bean.MemberSearchCondition;
import com.noonoo.prjtbackend.member.dto.MemberDto;
import com.noonoo.prjtbackend.member.dto.MemberSaveRequest;
import com.noonoo.prjtbackend.member.mapper.MemberMapper;
import com.noonoo.prjtbackend.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<MemberDto> findMembers(MemberSearchCondition condition) {
        return memberMapper.findMembers(condition);
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
    public Map<String, Object> createMember(MemberSaveRequest condition) {
        Map<String, Object> resultMap = new HashMap<>();
        log.info("=======> /api/members/createMember serviceimpl param={}",condition);
        String loginMemberId = RequestContext.getLoginMemberId();
        String clientIp = RequestContext.getClientIp();

        condition.setCreateId(loginMemberId != null ? loginMemberId : "SYSTEM");
        condition.setModifyId(loginMemberId != null ? loginMemberId : "SYSTEM");
        condition.setCreateIp(clientIp);
        condition.setModifyIp(clientIp);

        condition.setMemberPwd(passwordEncoder.encode(condition.getMemberPwd()));

        int cnt = memberMapper.insertMember(condition);

        resultMap.put("status", cnt > 0);
        resultMap.put("msg", cnt > 0 ? "success" : "fail");
        return resultMap;
    }

    @Override
    public Map<String, Object> updateMember(MemberSaveRequest condition) {
        Map<String, Object> resultMap = new HashMap<>();

        String loginMemberId = RequestContext.getLoginMemberId();
        String clientIp = RequestContext.getClientIp();

        condition.setModifyId(loginMemberId != null ? loginMemberId : "SYSTEM");
        condition.setModifyIp(clientIp);

        if (condition.getMemberPwd() != null && !condition.getMemberPwd().isBlank()) {
            condition.setMemberPwd(passwordEncoder.encode(condition.getMemberPwd()));
        }

        int cnt = memberMapper.updateMember(condition);

        resultMap.put("status", cnt > 0);
        resultMap.put("msg", cnt > 0 ? "success" : "fail");
        return resultMap;
    }

    @Override
    public Map<String, Object> deleteMember(Long memberSeq) {
        Map<String, Object> resultMap = new HashMap<>();
        int cnt = memberMapper.deleteMember(memberSeq);
        resultMap.put("status", cnt > 0);
        resultMap.put("msg", cnt > 0 ? "success" : "fail");
        return resultMap;
    }
}