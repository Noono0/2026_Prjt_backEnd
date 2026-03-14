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
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원 목록 검색
     */
    @PostMapping("/search")
    public ApiResponse<List<MemberDto>> searchMembers(@RequestBody MemberSearchCondition request) {
        log.info("=======> /api/members/search param={}",request);
        List<MemberDto> list = memberService.findMembers(request);
        return ApiResponse.ok("회원 목록 조회 완료", list);
    }

    /**
     * 회원 상세 조회
     */
    @GetMapping("/detail/{memberSeq}")
    public ApiResponse<MemberDto> findMemberDetail(@PathVariable Long memberSeq) {
        log.info("=======> /api/members/detail param={}",memberSeq);
        MemberDto detail = memberService.findMemberDetail(memberSeq);
        return ApiResponse.ok("회원 상세 조회 완료", detail);
    }

    /**
     * 회원 등록
     */
    @PostMapping("/create")
    public ApiResponse<Void> createMember(@RequestBody MemberSaveRequest request) {
        log.info("=======> /api/members/create param={}",request);
        memberService.createMember(request);
        return ApiResponse.ok("회원 등록 완료", null);
    }

    /**
     * 회원 수정
     */
    @PutMapping("/update")
    public ApiResponse<Void> updateMember(@RequestBody MemberSaveRequest request) {
        log.info("=======> /api/members/updateMember param={}",request);
        memberService.updateMember(request);
        return ApiResponse.ok("회원 수정 완료", null);
    }

    /**
     * 회원 삭제
     */
    @DeleteMapping("/delete/{memberSeq}")
    public ApiResponse<Void> deleteMember(@PathVariable Long memberSeq) {
        log.info("=======> /api/members/delete param={}",memberSeq);
        memberService.deleteMember(memberSeq);
        return ApiResponse.ok("회원 삭제 완료", null);
    }
}


