package com.noonoo.prjtbackend.member.controller;

import com.noonoo.prjtbackend.common.api.ApiResponse;
import com.noonoo.prjtbackend.common.paging.PageResponse;
import com.noonoo.prjtbackend.member.dto.MemberSearchCondition;
import com.noonoo.prjtbackend.member.dto.MemberDto;
import com.noonoo.prjtbackend.member.dto.PasswordChangeRequest;
import com.noonoo.prjtbackend.member.dto.MemberEmoticonDto;
import com.noonoo.prjtbackend.member.dto.MemberEmoticonSaveRequest;
import com.noonoo.prjtbackend.member.dto.MemberSaveRequest;
import com.noonoo.prjtbackend.member.service.MemberEmoticonService;
import com.noonoo.prjtbackend.member.service.MemberService;
import com.noonoo.prjtbackend.common.security.MenuAuthorities;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MemberEmoticonService memberEmoticonService;

    @GetMapping("/me/emoticons")
    @PreAuthorize("@securityExpressions.isAuthenticatedOrPermitAll()")
    public ApiResponse<java.util.List<MemberEmoticonDto>> myEmoticons() {
        return ApiResponse.ok("내 이모티콘 조회 완료", memberEmoticonService.findMyEmoticons());
    }

    @PostMapping("/me/emoticons")
    @PreAuthorize("@securityExpressions.isAuthenticatedOrPermitAll()")
    public ApiResponse<Integer> addMyEmoticon(@RequestBody MemberEmoticonSaveRequest request) {
        int n = memberEmoticonService.addMyEmoticon(request);
        return ApiResponse.ok(n > 0 ? "이모티콘이 등록되었습니다." : "등록에 실패했습니다.", n);
    }

    @DeleteMapping("/me/emoticons/{memberEmoticonSeq}")
    @PreAuthorize("@securityExpressions.isAuthenticatedOrPermitAll()")
    public ApiResponse<Integer> deleteMyEmoticon(@PathVariable Long memberEmoticonSeq) {
        int n = memberEmoticonService.deleteMyEmoticon(memberEmoticonSeq);
        return ApiResponse.ok(n > 0 ? "삭제되었습니다." : "삭제할 항목이 없습니다.", n);
    }

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

    @PutMapping("/me/password")
    @PreAuthorize("@securityExpressions.isAuthenticatedOrPermitAll()")
    public ApiResponse<Integer> changeMyPassword(@RequestBody PasswordChangeRequest request) {
        int result = memberService.changeMyPassword(request);
        return ApiResponse.ok(result > 0 ? "비밀번호 변경 완료" : "비밀번호 변경 실패", result);
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


