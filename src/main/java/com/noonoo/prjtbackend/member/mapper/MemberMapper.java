package com.noonoo.prjtbackend.member.mapper;

import com.noonoo.prjtbackend.member.dto.MemberSearchCondition;
import com.noonoo.prjtbackend.member.dto.MemberDto;
import com.noonoo.prjtbackend.member.dto.MemberSaveRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MemberMapper {
    List<MemberDto> findMembers(MemberSearchCondition condition);

    long findMembersCnt(MemberSearchCondition condition);

    MemberDto findMemberById(Long memberSeq);

    MemberDto findLoginMember(String memberId);

    MemberDto findIdCheck(MemberSaveRequest condition);

    int insertMember(MemberSaveRequest condition);

    int updateMember(MemberSaveRequest condition);

    int updateMemberPassword(@Param("memberSeq") Long memberSeq,
                             @Param("memberPwd") String memberPwd,
                             @Param("modifyId") String modifyId,
                             @Param("modifyIp") String modifyIp);

    int deleteMember(Long memberSeq);

}
