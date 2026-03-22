package com.noonoo.prjtbackend.menu.controller;

import com.noonoo.prjtbackend.common.api.ApiResponse;
import com.noonoo.prjtbackend.common.paging.PageResponse;
import com.noonoo.prjtbackend.menu.dto.MenuDto;
import com.noonoo.prjtbackend.menu.dto.MenuSaveRequest;
import com.noonoo.prjtbackend.menu.dto.MenuSearchCondition;
import com.noonoo.prjtbackend.common.security.MenuAuthorities;
import com.noonoo.prjtbackend.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/menus")
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.MENU + "')")
    public ApiResponse<List<MenuDto>> findMenus(@RequestParam String roleCode) {
        return ApiResponse.ok(menuService.findMenusByRole(roleCode));
    }

    /**
     * 메뉴관리 트리 UI용: 페이징 없이 USE_YN=Y 전체
     */
    @GetMapping("/tree")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.MENU + "')")
    public ApiResponse<List<MenuDto>> menuTree() {
        return ApiResponse.ok("메뉴 트리 조회 완료", menuService.findAllForTree());
    }

    @PostMapping("/search")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.MENU + "')")
    public ApiResponse<PageResponse<MenuDto>> searchMenus(@RequestBody MenuSearchCondition request) {
        PageResponse<MenuDto> result = menuService.selectList(request);
        return ApiResponse.ok("메뉴 목록 조회 완료", result);
    }

    @PostMapping("/detail")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.MENU + "')")
    public ApiResponse<MenuDto> findMenuDetail(@RequestBody MenuSearchCondition request) {
        MenuDto detail = menuService.selectDetail(request);
        return ApiResponse.ok("메뉴 상세 조회 완료", detail);
    }

    @PostMapping("/create")
    @PreAuthorize("@securityExpressions.canCreate('" + MenuAuthorities.MENU + "')")
    public ApiResponse<Integer> createMenu(@RequestBody MenuSaveRequest request) {
        int result = menuService.insertData(request);
        return ApiResponse.ok(result > 0 ? "메뉴 등록 완료" : "메뉴 등록 실패", result);
    }

    @PutMapping("/update")
    @PreAuthorize("@securityExpressions.canUpdate('" + MenuAuthorities.MENU + "')")
    public ApiResponse<Integer> updateMenu(@RequestBody MenuSaveRequest request) {
        int result = menuService.updateData(request);
        return ApiResponse.ok(result > 0 ? "메뉴 수정 완료" : "메뉴 수정 실패", result);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("@securityExpressions.canDelete('" + MenuAuthorities.MENU + "')")
    public ApiResponse<Integer> deleteMenu(@RequestBody MenuSaveRequest request) {
        int result = menuService.deleteData(request);
        return ApiResponse.ok(result > 0 ? "메뉴 삭제 완료" : "메뉴 삭제 실패", result);
    }
}
