package com.noonoo.prjtbackend.menu.service;

import com.noonoo.prjtbackend.menu.dto.MenuDto;

import java.util.List;

public interface MenuService {
    List<MenuDto> findMenusByRole(String roleCode);
}
