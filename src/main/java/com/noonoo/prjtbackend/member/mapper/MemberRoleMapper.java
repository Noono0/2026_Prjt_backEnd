package com.noonoo.prjtbackend.member.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MemberRoleMapper {

    List<String> findRoleCodesByMemberSeq(@Param("memberSeq") Long memberSeq);

    int insertMemberRole(
            @Param("memberSeq") Long memberSeq,
            @Param("roleCode") String roleCode,
            @Param("crtId") String crtId,
            @Param("crtIp") String crtIp
    );

    int deleteByMemberSeq(@Param("memberSeq") Long memberSeq);

    int deleteByMemberSeqAndRoleCode(
            @Param("memberSeq") Long memberSeq,
            @Param("roleCode") String roleCode
    );

    long countByMemberSeqAndRoleCode(
            @Param("memberSeq") Long memberSeq,
            @Param("roleCode") String roleCode
    );
}
