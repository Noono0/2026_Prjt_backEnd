package com.noonoo.prjtbackend.member.controller;

import com.noonoo.prjtbackend.common.api.ApiResponse;
import com.noonoo.prjtbackend.member.bean.MemberSearchCondition;
import com.noonoo.prjtbackend.member.dto.MemberDto;
import com.noonoo.prjtbackend.member.dto.MemberSaveRequest;
import com.noonoo.prjtbackend.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public ApiResponse<List<MemberDto>> findMembers(@RequestBody MemberSearchCondition condition) {
        log.info("findMembers controller 진입, request={}", condition);
        return ApiResponse.ok(memberService.findMembers(condition));
    }

    @GetMapping("/{memberId}")
    public ApiResponse<MemberDto> findMember(@PathVariable Long memberId) {
        log.info("findMember controller 진입, request={}", memberId);
        return ApiResponse.ok(memberService.findMemberById(memberId));
    }

    @GetMapping("/findIdCheck")
    public ApiResponse<MemberDto> findIdCheck(@RequestBody MemberSaveRequest condition) {
        log.info("findIdCheck controller 진입, request={}", condition);
        return ApiResponse.ok(memberService.findIdCheck(condition));
    }

    @PostMapping("/createMember")
    public ApiResponse<Map<String, Object>> createMember(@RequestBody MemberSaveRequest condition) {
        log.info("createMember controller 진입, request={}", condition);
        return ApiResponse.ok(memberService.createMember(condition));
    }

    @PutMapping("/updateMember")
    public ApiResponse<Map<String, Object>> updateMember(@RequestBody MemberSaveRequest condition) {
        log.info("updateMember controller 진입, request={}", condition);
        return ApiResponse.ok(memberService.updateMember(condition));
    }

    @DeleteMapping("/deleteMember")
    public ApiResponse<Map<String, Object>> deleteMember(@RequestBody MemberSaveRequest condition) {
        log.info("deleteMember controller 진입, request={}", condition);
        return ApiResponse.ok(memberService.deleteMember(condition));
    }


}
