package com.noonoo.prjtbackend.gamniverseprofile.controller;

import com.noonoo.prjtbackend.common.api.ApiResponse;
import com.noonoo.prjtbackend.common.paging.PageResponse;
import com.noonoo.prjtbackend.common.security.MenuAuthorities;
import com.noonoo.prjtbackend.gamniverseprofile.dto.GamniverseProfileDto;
import com.noonoo.prjtbackend.gamniverseprofile.dto.GamniverseProfileSaveRequest;
import com.noonoo.prjtbackend.gamniverseprofile.dto.GamniverseProfileSearchCondition;
import com.noonoo.prjtbackend.gamniverseprofile.service.GamniverseProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gamniverse-profiles")
@RequiredArgsConstructor
public class GamniverseProfileController {

    private final GamniverseProfileService gamniverseProfileService;

    @PostMapping("/search")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.MEMBER + "')")
    public ApiResponse<PageResponse<GamniverseProfileDto>> search(
            @RequestBody GamniverseProfileSearchCondition request) {
        return ApiResponse.ok("목록 조회 완료", gamniverseProfileService.search(request));
    }

    @GetMapping("/detail/{seq}")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.MEMBER + "')")
    public ApiResponse<GamniverseProfileDto> detail(@PathVariable Long seq) {
        return ApiResponse.ok("조회 완료", gamniverseProfileService.detail(seq));
    }

    @PostMapping("/create")
    @PreAuthorize("@securityExpressions.canCreate('" + MenuAuthorities.MEMBER + "')")
    public ApiResponse<Integer> create(@RequestBody GamniverseProfileSaveRequest request) {
        int n = gamniverseProfileService.create(request);
        return ApiResponse.ok(n > 0 ? "등록되었습니다." : "등록 실패", n);
    }

    @PutMapping("/update")
    @PreAuthorize("@securityExpressions.canUpdate('" + MenuAuthorities.MEMBER + "')")
    public ApiResponse<Integer> update(@RequestBody GamniverseProfileSaveRequest request) {
        int n = gamniverseProfileService.update(request);
        return ApiResponse.ok(n > 0 ? "수정되었습니다." : "수정 실패", n);
    }

    @DeleteMapping("/delete/{seq}")
    @PreAuthorize("@securityExpressions.canDelete('" + MenuAuthorities.MEMBER + "')")
    public ApiResponse<Integer> delete(@PathVariable Long seq) {
        int n = gamniverseProfileService.delete(seq);
        return ApiResponse.ok(n > 0 ? "삭제되었습니다." : "삭제 실패", n);
    }
}
