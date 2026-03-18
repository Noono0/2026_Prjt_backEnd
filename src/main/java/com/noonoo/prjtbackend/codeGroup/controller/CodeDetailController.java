package com.noonoo.prjtbackend.codeGroup.controller;

import com.noonoo.prjtbackend.codeGroup.dto.CodeDetailDto;
import com.noonoo.prjtbackend.codeGroup.dto.CodeDetailSaveRequest;
import com.noonoo.prjtbackend.codeGroup.dto.CodeDetailSearchCondition;
import com.noonoo.prjtbackend.codeGroup.service.CodeDetailService;
import com.noonoo.prjtbackend.common.api.ApiResponse;
import com.noonoo.prjtbackend.common.paging.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/code-details")
@RequiredArgsConstructor
public class CodeDetailController {

    private final CodeDetailService codeDetailService;

    @PostMapping("/search")
    public ApiResponse<PageResponse<CodeDetailDto>> searchCodeDetails(
            @RequestBody CodeDetailSearchCondition request) {
        log.info("=======> /api/code-details/search param={}", request);
        PageResponse<CodeDetailDto> result = codeDetailService.selectList(request);
        return ApiResponse.ok("코드디테일 목록 조회 완료", result);
    }

    @GetMapping("/detail/{codeDetailSeq}")
    public ApiResponse<CodeDetailDto> findCodeDetail(@PathVariable Long codeDetailSeq) {
        log.info("=======> /api/code-details/detail/{} ", codeDetailSeq);
        CodeDetailDto detail = codeDetailService.selectDetail(codeDetailSeq);
        return ApiResponse.ok("코드디테일 상세 조회 완료", detail);
    }

    @PostMapping("/create")
    public ApiResponse<Map<String, Object>> createCodeDetail(@RequestBody CodeDetailSaveRequest request) {
        log.info("=======> /api/code-details/create param={}", request);
        Map<String, Object> result = codeDetailService.insertData(request);
        return ApiResponse.ok("코드디테일 등록 완료", result);
    }

    @PutMapping("/update")
    public ApiResponse<Map<String, Object>> updateCodeDetail(@RequestBody CodeDetailSaveRequest request) {
        log.info("=======> /api/code-details/update param={}", request);
        Map<String, Object> result = codeDetailService.updateData(request);
        return ApiResponse.ok("코드디테일 수정 완료", result);
    }

    @DeleteMapping("/delete/{codeDetailSeq}")
    public ApiResponse<Map<String, Object>> deleteCodeDetail(@PathVariable Long codeDetailSeq) {
        log.info("=======> /api/code-details/delete/{}", codeDetailSeq);
        Map<String, Object> result = codeDetailService.deleteData(codeDetailSeq);
        return ApiResponse.ok("코드디테일 삭제 완료", result);
    }
}