package com.noonoo.prjtbackend.member.service;

import com.noonoo.prjtbackend.member.bean.MemberSearchCondition;
import com.noonoo.prjtbackend.member.dto.MemberDto;
import com.noonoo.prjtbackend.member.dto.MemberSaveRequest;

import java.util.List;
import java.util.Map;

public interface MemberService {
    List<MemberDto> findMembers(MemberSearchCondition condition);

    MemberDto findMemberDetail(Long memberSeq);

    MemberDto findIdCheck(MemberSaveRequest condition);

    Map<String, Object> createMember(MemberSaveRequest condition);

    Map<String, Object> updateMember(MemberSaveRequest condition);

    Map<String, Object> deleteMember(Long memberSeq);
}
