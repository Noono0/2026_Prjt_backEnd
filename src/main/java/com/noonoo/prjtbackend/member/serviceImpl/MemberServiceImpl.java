package com.noonoo.prjtbackend.member.serviceImpl;

import com.noonoo.prjtbackend.member.bean.MemberSearchCondition;
import com.noonoo.prjtbackend.member.dto.MemberDto;
import com.noonoo.prjtbackend.member.dto.MemberSaveRequest;
import com.noonoo.prjtbackend.member.mapper.MemberMapper;
import com.noonoo.prjtbackend.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;

    /**
     * 회원 목록 조회
     * MEMBER_READ 권한이 있는 사용자만 가능
     */
    @Override
    //@PreAuthorize("hasAuthority('MEMBER_READ')")
    public List<MemberDto> findMembers(MemberSearchCondition condition) {
        return memberMapper.findMembers(condition);
    }

    /**
     * 회원 상세 조회
     * MEMBER_READ 권한이 있는 사용자만 가능
     */
    @Override
    //@PreAuthorize("hasAuthority('MEMBER_READ')")
    public MemberDto findMemberById(Long memberId) {
        return memberMapper.findMemberById(memberId);
    }

    /**
     * 회원가입 ID Check
     *
     */
    @Override
    //@PreAuthorize("hasAuthority('MEMBER_READ')")
    public MemberDto findIdCheck(MemberSaveRequest condition) {
        return memberMapper.findIdCheck(condition);
    }

    /**
     * 회원 등록
     * MEMBER_CREATE 권한이 있는 사용자만 가능
     */
    @Override
    //@PreAuthorize("hasAuthority('MEMBER_CREATE')")
    public Map<String, Object> createMember(MemberSaveRequest condition) {
        Map<String, Object> resultMap = new HashMap();
        int cnt = memberMapper.insertMember(condition);
        resultMap.put("status", false);
        resultMap.put("msg","fail");
        if(cnt > 0) {
            resultMap.put("status", true);
            resultMap.put("msg","success");
        }

        return resultMap;
    }

    /**
     * 회원 수정
     * MEMBER_UPDATE 권한이 있는 사용자만 가능
     */
    @Override
    //@PreAuthorize("hasAuthority('MEMBER_UPDATE')")
    public Map<String, Object> updateMember(MemberSaveRequest condition) {
        Map<String, Object> resultMap = new HashMap();
        int cnt = memberMapper.updateMember(condition);
        resultMap.put("status", false);
        resultMap.put("msg","fail");
        if(cnt > 0) {
            resultMap.put("status", true);
            resultMap.put("msg","success");
        }
        return resultMap;
    }

    /**
     * 회원 삭제
     * MEMBER_DELETE 권한이 있는 사용자만 가능
     */
    @Override
    //@PreAuthorize("hasAuthority('MEMBER_DELETE')")
    public Map<String, Object> deleteMember(MemberSaveRequest condition) {
        Map<String, Object> resultMap = new HashMap();
        int cnt = memberMapper.deleteMember(condition);
        resultMap.put("status", false);
        resultMap.put("msg","fail");
        if(cnt > 0) {
            resultMap.put("status", true);
            resultMap.put("msg","success");
        }
        return resultMap;
    }
}