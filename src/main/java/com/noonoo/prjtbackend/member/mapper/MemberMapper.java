package com.noonoo.prjtbackend.member.mapper;

import com.noonoo.prjtbackend.member.bean.MemberSearchCondition;
import com.noonoo.prjtbackend.member.dto.MemberDto;
import com.noonoo.prjtbackend.member.dto.MemberLoginDto;
import com.noonoo.prjtbackend.member.dto.MemberSaveRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MemberMapper {
    List<MemberDto> findMembers(MemberSearchCondition condition);
    MemberDto findMemberById(Long memberId);

    MemberDto findLoginMember(String username);

    void insertMember(MemberSaveRequest request);

    void updateMember(@Param("memberId") Long memberId, @Param("request") MemberSaveRequest request);

    void deleteMember(@Param("memberId") Long memberId);

}
