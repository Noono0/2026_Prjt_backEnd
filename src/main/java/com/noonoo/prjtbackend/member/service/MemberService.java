package com.noonoo.prjtbackend.member.service;

import com.noonoo.prjtbackend.common.paging.PageResponse;
import com.noonoo.prjtbackend.member.dto.MemberSearchCondition;
import com.noonoo.prjtbackend.member.dto.MemberDto;
import com.noonoo.prjtbackend.member.dto.PasswordChangeRequest;
import com.noonoo.prjtbackend.member.dto.MemberSaveRequest;

public interface MemberService {
    PageResponse<MemberDto> findMembers(MemberSearchCondition condition);

    MemberDto findMemberDetail(Long memberSeq);

    MemberDto findIdCheck(MemberSaveRequest condition);

    int createMember(MemberSaveRequest condition);

    int updateMember(MemberSaveRequest condition);

    int changeMyPassword(PasswordChangeRequest request);

    int deleteMember(Long memberSeq);
}
