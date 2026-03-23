package com.noonoo.prjtbackend.menu.controller;

import com.noonoo.prjtbackend.common.api.ApiResponse;
import com.noonoo.prjtbackend.common.paging.PageResponse;
import com.noonoo.prjtbackend.menu.dto.MenuDto;
import com.noonoo.prjtbackend.menu.dto.MenuReorderItem;
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

    /**
     * 메인 레이아웃 사이드바 "추가 메뉴"용 (사용 중 메뉴만).
     * {@code /tree}는 MENU_READ 필요 — 사이드바는 레이아웃에서 비로그인 상태로도 호출되므로 공개 허용.
     * (경로·이름 수준의 네비 메타만 반환)
     */
    @GetMapping("/sidebar")
    @PreAuthorize("permitAll()")
    public ApiResponse<List<MenuDto>> sidebarMenus() {
        return ApiResponse.ok("사이드바 메뉴", menuService.findActiveForSidebar());
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

    /**
     * 트리 드래그 순서/부모만 반영. 메뉴코드·이름은 건드리지 않으며 중복코드 검사를 하지 않음.
     */
    @PutMapping("/reorder")
    @PreAuthorize("@securityExpressions.canUpdate('" + MenuAuthorities.MENU + "')")
    public ApiResponse<Integer> reorderMenus(@RequestBody List<MenuReorderItem> items) {
        int result = menuService.reorderMenus(items);
        return ApiResponse.ok("메뉴 순서 반영 완료", result);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("@securityExpressions.canDelete('" + MenuAuthorities.MENU + "')")
    public ApiResponse<Integer> deleteMenu(@RequestBody MenuSaveRequest request) {
        int result = menuService.deleteData(request);
        return ApiResponse.ok(result > 0 ? "메뉴 삭제 완료" : "메뉴 삭제 실패", result);
    }
}
