package com.noonoo.prjtbackend.member.mapper;

import com.noonoo.prjtbackend.member.bean.MemberSearchCondition;
import com.noonoo.prjtbackend.member.dto.MemberDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemberMapper {
    List<MemberDto> findMembers(MemberSearchCondition condition);
    MemberDto findMemberById(Long memberId);
}
