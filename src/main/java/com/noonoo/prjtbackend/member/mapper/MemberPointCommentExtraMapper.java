package com.noonoo.prjtbackend.member.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberPointCommentExtraMapper {

    Integer selectExtraPointsEarned(
            @Param("memberSeq") long memberSeq,
            @Param("postType") String postType,
            @Param("postSeq") long postSeq);

    int upsertExtraPoints(
            @Param("memberSeq") long memberSeq,
            @Param("postType") String postType,
            @Param("postSeq") long postSeq,
            @Param("addPoints") int addPoints);
}
