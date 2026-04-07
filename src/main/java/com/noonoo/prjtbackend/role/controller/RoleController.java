package com.noonoo.prjtbackend.role.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.noonoo.prjtbackend.common.api.ApiResponse;
import com.noonoo.prjtbackend.common.paging.PageResponse;
import com.noonoo.prjtbackend.role.dto.RoleDto;
import com.noonoo.prjtbackend.role.dto.RoleSaveRequest;
import com.noonoo.prjtbackend.common.security.MenuAuthorities;
import com.noonoo.prjtbackend.role.dto.RoleSearchCondition;
import com.noonoo.prjtbackend.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    /**
     * 기존 호환: 활성 권한 목록 (회원 폼 등)
     */
    @GetMapping
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.ROLE + "')")
    public Map<String, Object> findRoles() {
        List<RoleDto> items = roleService.findAllActiveRoles();

        Map<String, Object> result = new HashMap<>();
        result.put("items", items);
        result.put("total", items.size());
        return result;
    }

    @PostMapping("/search")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.ROLE + "')")
    public ApiResponse<PageResponse<RoleDto>> searchRoles(@RequestBody RoleSearchCondition request) {
        PageResponse<RoleDto> result = roleService.selectList(request);
        return ApiResponse.ok("권한 목록 조회 완료", result);
    }

    @PostMapping("/detail")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.ROLE + "')")
    public ApiResponse<RoleDto> findRoleDetail(@RequestBody RoleSearchCondition request) {
        RoleDto detail = roleService.selectDetail(request);
        return ApiResponse.ok("권한 상세 조회 완료", detail);
    }

    @PostMapping("/create")
    @PreAuthorize("@securityExpressions.canCreate('" + MenuAuthorities.ROLE + "')")
    public ApiResponse<Integer> createRole(@RequestBody RoleSaveRequest request) {
        int result = roleService.insertData(request);
        return ApiResponse.ok(result > 0 ? "권한 등록 완료" : "권한 등록 실패", result);
    }

    @PutMapping("/update")
    @PreAuthorize("@securityExpressions.canUpdate('" + MenuAuthorities.ROLE + "')")
    public ApiResponse<Integer> updateRole(@RequestBody RoleSaveRequest request) {
        int result = roleService.updateData(request);
        return ApiResponse.ok(result > 0 ? "권한 수정 완료" : "권한 수정 실패", result);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("@securityExpressions.canDelete('" + MenuAuthorities.ROLE + "')")
    public ApiResponse<Integer> deleteRole(@RequestBody RoleSaveRequest request) {
        int result = roleService.deleteData(request);
        return ApiResponse.ok(result > 0 ? "권한 삭제 완료" : "권한 삭제 실패", result);
    }
}
