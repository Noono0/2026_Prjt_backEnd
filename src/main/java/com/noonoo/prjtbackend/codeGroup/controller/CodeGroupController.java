package com.noonoo.prjtbackend.codeGroup.controller;

import com.noonoo.prjtbackend.codeGroup.dto.CodeGroupDto;
import com.noonoo.prjtbackend.codeGroup.dto.CodeGroupSaveRequest;
import com.noonoo.prjtbackend.codeGroup.dto.CodeGroupSearchCondition;
import com.noonoo.prjtbackend.codeGroup.dto.OptionDto;
import com.noonoo.prjtbackend.codeGroup.service.CodeGroupService;
import com.noonoo.prjtbackend.common.api.ApiResponse;
import com.noonoo.prjtbackend.common.paging.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/code-groups")
@RequiredArgsConstructor
public class CodeGroupController {

    private final CodeGroupService codeGroupService;

    @PostMapping("/search")
    public ApiResponse<PageResponse<CodeGroupDto>> searchCodeGroups(
            @RequestBody CodeGroupSearchCondition request) {
        log.info("=======> /api/code-groups/search param={}", request);
        PageResponse<CodeGroupDto> result = codeGroupService.selectList(request);
        return ApiResponse.ok("코드그룹 목록 조회 완료", result);
    }

    @GetMapping("/detail/{codeGroupSeq}")
    public ApiResponse<CodeGroupDto> findCodeGroupDetail(@PathVariable Long codeGroupSeq) {
        log.info("=======> /api/code-groups/detail/{}", codeGroupSeq);
        CodeGroupDto detail = codeGroupService.selectDetail(codeGroupSeq);
        return ApiResponse.ok("코드그룹 상세 조회 완료", detail);
    }

    @PostMapping("/create")
    public ApiResponse<Map<String, Object>> createCodeGroup(@RequestBody CodeGroupSaveRequest request) {
        log.info("=======> /api/code-groups/create param={}", request);
        Map<String, Object> result = codeGroupService.insertData(request);
        return ApiResponse.ok(
                Boolean.TRUE.equals(result.get("status")) ? "코드그룹 등록 완료" : "코드그룹 등록 실패",
                result
        );
    }

    @PutMapping("/update")
    public ApiResponse<Map<String, Object>> updateCodeGroup(@RequestBody CodeGroupSaveRequest request) {
        log.info("=======> /api/code-groups/update param={}", request);
        Map<String, Object> result = codeGroupService.updateData(request);
        return ApiResponse.ok(
                Boolean.TRUE.equals(result.get("status")) ? "코드그룹 수정 완료" : "코드그룹 수정 실패",
                result
        );
    }

    @DeleteMapping("/delete/{codeGroupSeq}")
    public ApiResponse<Map<String, Object>> deleteCodeGroup(@PathVariable Long codeGroupSeq) {
        log.info("=======> /api/code-groups/delete/{}", codeGroupSeq);
        Map<String, Object> result = codeGroupService.deleteData(codeGroupSeq);
        return ApiResponse.ok(
                Boolean.TRUE.equals(result.get("status")) ? "코드그룹 삭제 완료" : "코드그룹 삭제 실패",
                result
        );
    }

    @GetMapping("/options")
    public ApiResponse<List<OptionDto>> findCodeGroupOptions() {
        return ApiResponse.ok("코드그룹 옵션 조회 완료", codeGroupService.findCodeGroupOptions());
    }
}