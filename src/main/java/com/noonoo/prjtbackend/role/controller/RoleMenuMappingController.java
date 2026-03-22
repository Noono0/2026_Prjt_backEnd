package com.noonoo.prjtbackend.role.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.noonoo.prjtbackend.common.api.ApiResponse;
import com.noonoo.prjtbackend.common.security.MenuAuthorities;
import com.noonoo.prjtbackend.role.dto.RoleMenuAssignmentDto;
import com.noonoo.prjtbackend.role.dto.RoleMenuMappingQueryRequest;
import com.noonoo.prjtbackend.role.dto.RoleMenuMappingSaveRequest;
import com.noonoo.prjtbackend.role.service.RoleMenuMappingService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/roles/menu-mappings")
public class RoleMenuMappingController {

    private final RoleMenuMappingService roleMenuMappingService;

    /**
     * 역할별 전체 메뉴 + 현재 매핑(ROLE_MENU) 조회
     */
    @PostMapping
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.ROLE + "')")
    public ApiResponse<List<RoleMenuAssignmentDto>> query(@RequestBody RoleMenuMappingQueryRequest request) {
        List<RoleMenuAssignmentDto> list = roleMenuMappingService.selectAssignments(request);
        return ApiResponse.ok("역할-메뉴 매핑 조회 완료", list);
    }

    /**
     * 역할에 대한 ROLE_MENU 전체 교체 저장 (해당 역할의 기존 매핑 삭제 후 재등록)
     */
    @PutMapping
    @PreAuthorize("@securityExpressions.canUpdate('" + MenuAuthorities.ROLE + "')")
    public ApiResponse<Void> save(@RequestBody RoleMenuMappingSaveRequest request) {
        roleMenuMappingService.saveAssignments(request);
        return ApiResponse.ok("역할-메뉴 매핑 저장 완료", null);
    }
}
