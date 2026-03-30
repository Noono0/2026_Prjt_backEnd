package com.noonoo.prjtbackend.contentfilter.controller;

import com.noonoo.prjtbackend.common.api.ApiResponse;
import com.noonoo.prjtbackend.common.paging.PageResponse;
import com.noonoo.prjtbackend.common.security.MenuAuthorities;
import com.noonoo.prjtbackend.contentfilter.dto.ContentFilterWordDto;
import com.noonoo.prjtbackend.contentfilter.dto.ContentFilterWordSaveRequest;
import com.noonoo.prjtbackend.contentfilter.dto.ContentFilterWordSearchCondition;
import com.noonoo.prjtbackend.contentfilter.service.ContentFilterWordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/content-filter-words")
@RequiredArgsConstructor
public class ContentFilterWordController {

    private final ContentFilterWordService contentFilterWordService;

    @PostMapping("/search")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.CONTENT_FILTER + "')")
    public ApiResponse<PageResponse<ContentFilterWordDto>> search(@RequestBody ContentFilterWordSearchCondition request) {
        return ApiResponse.ok("목록 조회 완료", contentFilterWordService.search(request));
    }

    @GetMapping("/detail/{seq}")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.CONTENT_FILTER + "')")
    public ApiResponse<ContentFilterWordDto> detail(@PathVariable Long seq) {
        ContentFilterWordDto dto = contentFilterWordService.detail(seq);
        if (dto == null || !"Y".equalsIgnoreCase(dto.getUseYn())) {
            return ApiResponse.fail("NOT_FOUND", "항목을 찾을 수 없습니다.");
        }
        return ApiResponse.ok("조회 완료", dto);
    }

    @PostMapping("/create")
    @PreAuthorize("@securityExpressions.canCreate('" + MenuAuthorities.CONTENT_FILTER + "')")
    public ApiResponse<Integer> create(@RequestBody ContentFilterWordSaveRequest request) {
        int n = contentFilterWordService.create(request);
        return ApiResponse.ok(n > 0 ? "등록되었습니다." : "등록 실패", n);
    }

    @PutMapping("/update")
    @PreAuthorize("@securityExpressions.canUpdate('" + MenuAuthorities.CONTENT_FILTER + "')")
    public ApiResponse<Integer> update(@RequestBody ContentFilterWordSaveRequest request) {
        int n = contentFilterWordService.update(request);
        return ApiResponse.ok(n > 0 ? "수정되었습니다." : "수정 실패", n);
    }

    @DeleteMapping("/delete/{seq}")
    @PreAuthorize("@securityExpressions.canDelete('" + MenuAuthorities.CONTENT_FILTER + "')")
    public ApiResponse<Integer> delete(@PathVariable Long seq) {
        int n = contentFilterWordService.delete(seq);
        return ApiResponse.ok(n > 0 ? "삭제되었습니다." : "삭제 실패", n);
    }
}
