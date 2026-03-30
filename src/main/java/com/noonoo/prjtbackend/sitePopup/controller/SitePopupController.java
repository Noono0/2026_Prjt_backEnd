package com.noonoo.prjtbackend.sitePopup.controller;

import com.noonoo.prjtbackend.common.api.ApiResponse;
import com.noonoo.prjtbackend.common.paging.PageResponse;
import com.noonoo.prjtbackend.common.security.MenuAuthorities;
import com.noonoo.prjtbackend.sitePopup.dto.SitePopupDto;
import com.noonoo.prjtbackend.sitePopup.dto.SitePopupSaveRequest;
import com.noonoo.prjtbackend.sitePopup.dto.SitePopupSearchCondition;
import com.noonoo.prjtbackend.sitePopup.service.SitePopupService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/site-popups")
@RequiredArgsConstructor
public class SitePopupController {

    private final SitePopupService sitePopupService;

    @GetMapping("/active")
    @PreAuthorize("permitAll()")
    public ApiResponse<List<SitePopupDto>> findActiveForSiteLoad() {
        List<SitePopupDto> items = sitePopupService.findSitePopupsForSiteLoad();
        return ApiResponse.ok("사이트 팝업 목록 조회 완료", items);
    }

    @GetMapping("/public/{sitePopupSeq}")
    @PreAuthorize("permitAll()")
    public ApiResponse<SitePopupDto> findPublic(@PathVariable Long sitePopupSeq) {
        SitePopupDto dto = sitePopupService.findSitePopupPublic(sitePopupSeq);
        if (dto == null) {
            return ApiResponse.fail("NOT_FOUND", "팝업을 찾을 수 없습니다.");
        }
        return ApiResponse.ok("사이트 팝업 조회 완료", dto);
    }

    @PostMapping("/search")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.SITE_POPUP + "')")
    public ApiResponse<PageResponse<SitePopupDto>> search(@RequestBody SitePopupSearchCondition request) {
        PageResponse<SitePopupDto> result = sitePopupService.findSitePopups(request);
        return ApiResponse.ok("팝업 목록 조회 완료", result);
    }

    @GetMapping("/detail/{sitePopupSeq}")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.SITE_POPUP + "')")
    public ApiResponse<SitePopupDto> detail(@PathVariable Long sitePopupSeq) {
        SitePopupDto dto = sitePopupService.findSitePopupDetail(sitePopupSeq);
        return ApiResponse.ok("팝업 상세 조회 완료", dto);
    }

    @PostMapping("/create")
    @PreAuthorize("@securityExpressions.canCreate('" + MenuAuthorities.SITE_POPUP + "')")
    public ApiResponse<Integer> create(@RequestBody SitePopupSaveRequest request) {
        int n = sitePopupService.createSitePopup(request);
        return ApiResponse.ok(n > 0 ? "팝업 등록 완료" : "팝업 등록 실패", n);
    }

    @PutMapping("/update")
    @PreAuthorize("@securityExpressions.canUpdate('" + MenuAuthorities.SITE_POPUP + "')")
    public ApiResponse<Integer> update(@RequestBody SitePopupSaveRequest request) {
        int n = sitePopupService.updateSitePopup(request);
        return ApiResponse.ok(n > 0 ? "팝업 수정 완료" : "팝업 수정 실패", n);
    }

    @DeleteMapping("/delete/{sitePopupSeq}")
    @PreAuthorize("@securityExpressions.canDelete('" + MenuAuthorities.SITE_POPUP + "')")
    public ApiResponse<Integer> delete(@PathVariable Long sitePopupSeq) {
        int n = sitePopupService.deleteSitePopup(sitePopupSeq);
        return ApiResponse.ok(n > 0 ? "팝업 삭제 완료" : "팝업 삭제 실패", n);
    }
}
