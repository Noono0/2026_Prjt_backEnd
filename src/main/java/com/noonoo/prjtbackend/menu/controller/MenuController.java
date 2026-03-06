package com.noonoo.prjtbackend.menu.controller;

import com.noonoo.prjtbackend.common.api.ApiResponse;
import com.noonoo.prjtbackend.menu.dto.MenuDto;
import com.noonoo.prjtbackend.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ApiResponse<List<MenuDto>> findMenus(@RequestParam String roleCode) {
        return ApiResponse.ok(menuService.findMenusByRole(roleCode));
    }
}
