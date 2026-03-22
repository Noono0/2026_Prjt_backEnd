package com.noonoo.prjtbackend.member.controller;

import com.noonoo.prjtbackend.common.api.ApiResponse;
import com.noonoo.prjtbackend.common.paging.PageResponse;
import com.noonoo.prjtbackend.member.dto.MemberSearchCondition;
import com.noonoo.prjtbackend.member.dto.MemberDto;
import com.noonoo.prjtbackend.member.dto.MemberSaveRequest;
import com.noonoo.prjtbackend.member.service.MemberService;
import com.noonoo.prjtbackend.common.security.MenuAuthorities;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.MEMBER + "')")
    public ApiResponse<PageResponse<MemberDto>> searchMembers(@RequestBody MemberSearchCondition request) {
        log.info("=======> /api/members/search param={}", request);
        PageResponse<MemberDto> result = memberService.findMembers(request);
        return ApiResponse.ok("회원 목록 조회 완료", result);
    }

    /**
     * 회원 상세 조회
     */
    @GetMapping("/detail/{memberSeq}")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.MEMBER + "')")
    public ApiResponse<MemberDto> findMemberDetail(@PathVariable Long memberSeq) {
        log.info("=======> /api/members/detail param={}", memberSeq);
        MemberDto detail = memberService.findMemberDetail(memberSeq);
        return ApiResponse.ok("회원 상세 조회 완료", detail);
    }

    /**
     * 회원 등록
     */
    @PostMapping("/create")
    @PreAuthorize("@securityExpressions.canCreate('" + MenuAuthorities.MEMBER + "')")
    public ApiResponse<Integer> createMember(@RequestBody MemberSaveRequest request) {
        log.info("=======> /api/members/create param={}", request);
        int result = memberService.createMember(request);
        return ApiResponse.ok(result > 0 ? "회원 등록 완료" : "회원 등록 실패", result);
    }

    /**
     * 회원 수정
     */
    @PutMapping("/update")
    @PreAuthorize("@securityExpressions.canUpdate('" + MenuAuthorities.MEMBER + "')")
    public ApiResponse<Integer> updateMember(@RequestBody MemberSaveRequest request) {
        log.info("=======> /api/members/update param={}", request);
        int result = memberService.updateMember(request);
        return ApiResponse.ok(result > 0 ? "회원 수정 완료" : "회원 수정 실패", result);
    }

    /**
     * 회원 삭제
     */
    @DeleteMapping("/delete/{memberSeq}")
    @PreAuthorize("@securityExpressions.canDelete('" + MenuAuthorities.MEMBER + "')")
    public ApiResponse<Integer> deleteMember(@PathVariable Long memberSeq) {
        log.info("=======> /api/members/delete param={}", memberSeq);
        int result = memberService.deleteMember(memberSeq);
        return ApiResponse.ok(result > 0 ? "회원 삭제 완료" : "회원 삭제 실패", result);
    }
}


