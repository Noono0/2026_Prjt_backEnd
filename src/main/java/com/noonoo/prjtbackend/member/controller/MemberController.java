package com.noonoo.prjtbackend.member.controller;

import com.noonoo.prjtbackend.common.api.ApiResponse;
import com.noonoo.prjtbackend.member.bean.MemberSearchCondition;
import com.noonoo.prjtbackend.member.dto.MemberDto;
import com.noonoo.prjtbackend.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public ApiResponse<List<MemberDto>> findMembers(MemberSearchCondition condition) {
        return ApiResponse.ok(memberService.findMembers(condition));
    }

    @GetMapping("/{memberId}")
    public ApiResponse<MemberDto> findMember(@PathVariable Long memberId) {
        return ApiResponse.ok(memberService.findMemberById(memberId));
    }
}
