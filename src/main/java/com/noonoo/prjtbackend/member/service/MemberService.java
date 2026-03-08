package com.noonoo.prjtbackend.member.service;

import com.noonoo.prjtbackend.member.bean.MemberSearchCondition;
import com.noonoo.prjtbackend.member.dto.MemberDto;
import com.noonoo.prjtbackend.member.dto.MemberSaveRequest;

import java.util.List;

public interface MemberService {
    List<MemberDto> findMembers(MemberSearchCondition condition);

    MemberDto findMemberById(Long memberId);

    void createMember(MemberSaveRequest request);

    void updateMember(Long memberId, MemberSaveRequest request);

    void deleteMember(Long memberId);
}
