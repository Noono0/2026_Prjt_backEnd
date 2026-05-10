package com.noonoo.prjtbackend.sitesupport.controller;

import com.noonoo.prjtbackend.common.api.ApiResponse;
import com.noonoo.prjtbackend.common.paging.PageResponse;
import com.noonoo.prjtbackend.common.security.MenuAuthorities;
import com.noonoo.prjtbackend.sitesupport.dto.SiteSupportDto;
import com.noonoo.prjtbackend.sitesupport.dto.SiteSupportSaveRequest;
import com.noonoo.prjtbackend.sitesupport.dto.SiteSupportSearchCondition;
import com.noonoo.prjtbackend.sitesupport.service.SiteSupportService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/site-support")
@RequiredArgsConstructor
public class SiteSupportController {

    private final SiteSupportService siteSupportService;

    @GetMapping("/active")
    @PreAuthorize("permitAll()")
    public ApiResponse<List<SiteSupportDto>> activeForSite() {
        return ApiResponse.ok("서포트 목록 조회 완료", siteSupportService.findActiveForSite());
    }

    @PostMapping("/search")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.SITE_SUPPORT + "')")
    public ApiResponse<PageResponse<SiteSupportDto>> search(@RequestBody SiteSupportSearchCondition request) {
        return ApiResponse.ok("서포트 관리 목록 조회 완료", siteSupportService.search(request));
    }

    @GetMapping("/detail/{supportSeq}")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.SITE_SUPPORT + "')")
    public ApiResponse<SiteSupportDto> detail(@PathVariable Long supportSeq) {
        SiteSupportDto dto = siteSupportService.detail(supportSeq);
        if (dto == null) {
            return ApiResponse.fail("NOT_FOUND", "항목을 찾을 수 없습니다.");
        }
        return ApiResponse.ok("서포트 상세 조회 완료", dto);
    }

    @PostMapping("/create")
    @PreAuthorize("@securityExpressions.canCreate('" + MenuAuthorities.SITE_SUPPORT + "')")
    public ApiResponse<Integer> create(@RequestBody SiteSupportSaveRequest request) {
        try {
            int n = siteSupportService.create(request);
            return ApiResponse.ok(n > 0 ? "등록 완료" : "등록 실패", n);
        } catch (IllegalArgumentException ex) {
            return ApiResponse.fail("BAD_REQUEST", ex.getMessage());
        }
    }

    @PutMapping("/update")
    @PreAuthorize("@securityExpressions.canUpdate('" + MenuAuthorities.SITE_SUPPORT + "')")
    public ApiResponse<Integer> update(@RequestBody SiteSupportSaveRequest request) {
        try {
            int n = siteSupportService.update(request);
            return ApiResponse.ok(n > 0 ? "수정 완료" : "수정 실패", n);
        } catch (IllegalArgumentException ex) {
            return ApiResponse.fail("BAD_REQUEST", ex.getMessage());
        }
    }

    @DeleteMapping("/delete/{supportSeq}")
    @PreAuthorize("@securityExpressions.canDelete('" + MenuAuthorities.SITE_SUPPORT + "')")
    public ApiResponse<Integer> delete(@PathVariable Long supportSeq) {
        int n = siteSupportService.delete(supportSeq);
        return ApiResponse.ok(n > 0 ? "삭제 완료" : "삭제 실패", n);
    }
}
