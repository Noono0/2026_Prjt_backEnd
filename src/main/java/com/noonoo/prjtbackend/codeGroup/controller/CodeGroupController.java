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

    @PostMapping("/detail")
    public ApiResponse<CodeGroupDto> findCodeGroupDetail(@RequestBody CodeGroupSearchCondition request) {
        log.info("=======> /api/code-groups/detail param={}", request);
        CodeGroupDto detail = codeGroupService.selectDetail(request);
        return ApiResponse.ok("코드그룹 상세 조회 완료", detail);
    }

    @PostMapping("/create")
    public ApiResponse<Integer> createCodeGroup(@RequestBody CodeGroupSaveRequest request) {
        log.info("=======> /api/code-groups/create param={}", request);
        int result = codeGroupService.insertData(request);
        return ApiResponse.ok(result > 0 ? "코드그룹 등록 완료" : "코드그룹 등록 실패", result);
    }

    @PutMapping("/update")
    public ApiResponse<Integer> updateCodeGroup(@RequestBody CodeGroupSaveRequest request) {
        log.info("=======> /api/code-groups/update param={}", request);
        int result = codeGroupService.updateData(request);
        return ApiResponse.ok(result > 0 ? "코드그룹 수정 완료" : "코드그룹 수정 실패", result);
    }

    @DeleteMapping("/delete")
    public ApiResponse<Integer> deleteCodeGroup(@RequestBody CodeGroupSaveRequest request) {
        log.info("=======> /api/code-groups/delete param={}", request);
        int result = codeGroupService.deleteData(request);
        return ApiResponse.ok(result > 0 ? "코드그룹 삭제 완료" : "코드그룹 삭제 실패", result);
    }

    @GetMapping("/options")
    public ApiResponse<List<OptionDto>> findCodeGroupOptions() {
        return ApiResponse.ok("코드그룹 옵션 조회 완료", codeGroupService.findCodeGroupOptions());
    }
}