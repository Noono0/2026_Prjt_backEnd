package com.noonoo.prjtbackend.member.mapper;

import com.noonoo.prjtbackend.member.dto.MemberStreamerProfileDto;
import com.noonoo.prjtbackend.member.dto.MemberStreamerProfileSaveRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberStreamerProfileMapper {

    MemberStreamerProfileDto selectByMemberSeq(@Param("memberSeq") long memberSeq);

    int insertProfile(
            @Param("memberSeq") long memberSeq,
            @Param("p") MemberStreamerProfileSaveRequest p);

    int updateProfile(
            @Param("memberSeq") long memberSeq,
            @Param("p") MemberStreamerProfileSaveRequest p);

    int deleteByMemberSeq(@Param("memberSeq") long memberSeq);

    int countByMemberSeq(@Param("memberSeq") long memberSeq);
}
