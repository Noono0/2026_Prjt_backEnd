package com.noonoo.prjtbackend.member.serviceImpl;

import com.noonoo.prjtbackend.member.bean.MemberSearchCondition;
import com.noonoo.prjtbackend.member.dto.MemberDto;
import com.noonoo.prjtbackend.member.mapper.MemberMapper;
import com.noonoo.prjtbackend.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;

    @Override
    public List<MemberDto> findMembers(MemberSearchCondition condition) {
        return memberMapper.findMembers(condition);
    }

    @Override
    public MemberDto findMemberById(Long memberId) {
        return memberMapper.findMemberById(memberId);
    }
}
