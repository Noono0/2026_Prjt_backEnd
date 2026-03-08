package com.noonoo.prjtbackend.member.serviceImpl;

import com.noonoo.prjtbackend.member.bean.MemberSearchCondition;
import com.noonoo.prjtbackend.member.dto.MemberDto;
import com.noonoo.prjtbackend.member.dto.MemberSaveRequest;
import com.noonoo.prjtbackend.member.mapper.MemberMapper;
import com.noonoo.prjtbackend.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;

    /**
     * 회원 목록 조회
     * MEMBER_READ 권한이 있는 사용자만 가능
     */
    @Override
    @PreAuthorize("hasAuthority('MEMBER_READ')")
    public List<MemberDto> findMembers(MemberSearchCondition condition) {
        return memberMapper.findMembers(condition);
    }

    /**
     * 회원 상세 조회
     * MEMBER_READ 권한이 있는 사용자만 가능
     */
    @Override
    @PreAuthorize("hasAuthority('MEMBER_READ')")
    public MemberDto findMemberById(Long memberId) {
        return memberMapper.findMemberById(memberId);
    }

    /**
     * 회원 등록
     * MEMBER_CREATE 권한이 있는 사용자만 가능
     */
    @Override
    @PreAuthorize("hasAuthority('MEMBER_CREATE')")
    public void createMember(MemberSaveRequest request) {
        memberMapper.insertMember(request);
    }

    /**
     * 회원 수정
     * MEMBER_UPDATE 권한이 있는 사용자만 가능
     */
    @Override
    @PreAuthorize("hasAuthority('MEMBER_UPDATE')")
    public void updateMember(Long memberId, MemberSaveRequest request) {
        memberMapper.updateMember(memberId, request);
    }

    /**
     * 회원 삭제
     * MEMBER_DELETE 권한이 있는 사용자만 가능
     */
    @Override
    @PreAuthorize("hasAuthority('MEMBER_DELETE')")
    public void deleteMember(Long memberId) {
        memberMapper.deleteMember(memberId);
    }
}