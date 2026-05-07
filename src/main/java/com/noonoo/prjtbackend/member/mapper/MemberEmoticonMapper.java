package com.noonoo.prjtbackend.member.mapper;

import com.noonoo.prjtbackend.member.dto.MemberEmoticonDto;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberEmoticonMapper {

    List<MemberEmoticonDto> findByMemberSeq(@Param("memberSeq") Long memberSeq);

    int countByMemberSeq(@Param("memberSeq") Long memberSeq);

    int insert(
            @Param("memberSeq") Long memberSeq,
            @Param("imageUrl") String imageUrl,
            @Param("sortOrder") int sortOrder);

    int deleteBySeqAndMember(
            @Param("memberEmoticonSeq") Long memberEmoticonSeq, @Param("memberSeq") Long memberSeq);
}
