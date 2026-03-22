package com.noonoo.prjtbackend.codeGroup.controller;

import com.noonoo.prjtbackend.codeGroup.dto.CodeDetailDto;
import com.noonoo.prjtbackend.codeGroup.dto.CodeDetailSaveRequest;
import com.noonoo.prjtbackend.codeGroup.dto.CodeDetailSearchCondition;
import com.noonoo.prjtbackend.codeGroup.service.CodeDetailService;
import com.noonoo.prjtbackend.common.api.ApiResponse;
import com.noonoo.prjtbackend.common.paging.PageResponse;
import com.noonoo.prjtbackend.common.security.MenuAuthorities;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/code-details")
@RequiredArgsConstructor
public class CodeDetailController {

    private final CodeDetailService codeDetailService;

    @PostMapping("/search")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.CODE_DETAIL + "')")
    public ApiResponse<PageResponse<CodeDetailDto>> searchCodeDetails(
            @RequestBody CodeDetailSearchCondition request) {
        log.info("=======> /api/code-details/search param={}", request);
        PageResponse<CodeDetailDto> result = codeDetailService.selectList(request);
        return ApiResponse.ok("코드디테일 목록 조회 완료", result);
    }

    @PostMapping("/detail")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.CODE_DETAIL + "')")
    public ApiResponse<CodeDetailDto> findCodeDetail(@RequestBody CodeDetailSearchCondition request) {
        log.info("=======> /api/code-details/detail param={}", request);
        CodeDetailDto detail = codeDetailService.selectDetail(request);
        return ApiResponse.ok("코드디테일 상세 조회 완료", detail);
    }

    @PostMapping("/create")
    @PreAuthorize("@securityExpressions.canCreate('" + MenuAuthorities.CODE_DETAIL + "')")
    public ApiResponse<Integer> createCodeDetail(@RequestBody CodeDetailSaveRequest request) {
        log.info("=======> /api/code-details/create param={}", request);
        int result = codeDetailService.insertData(request);
        return ApiResponse.ok(result > 0 ? "코드디테일 등록 완료" : "코드디테일 등록 실패", result);
    }

    @PutMapping("/update")
    @PreAuthorize("@securityExpressions.canUpdate('" + MenuAuthorities.CODE_DETAIL + "')")
    public ApiResponse<Integer> updateCodeDetail(@RequestBody CodeDetailSaveRequest request) {
        log.info("=======> /api/code-details/update param={}", request);
        int result = codeDetailService.updateData(request);
        return ApiResponse.ok(result > 0 ? "코드디테일 수정 완료" : "코드디테일 수정 실패", result);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("@securityExpressions.canDelete('" + MenuAuthorities.CODE_DETAIL + "')")
    public ApiResponse<Integer> deleteCodeDetail(@RequestBody CodeDetailSaveRequest request) {
        log.info("=======> /api/code-details/delete param={}", request);
        int result = codeDetailService.deleteData(request);
        return ApiResponse.ok(result > 0 ? "코드디테일 삭제 완료" : "코드디테일 삭제 실패", result);
    }
}