package com.noonoo.prjtbackend.menu.service;

import com.noonoo.prjtbackend.common.paging.PageResponse;
import com.noonoo.prjtbackend.menu.dto.MenuDto;
import com.noonoo.prjtbackend.menu.dto.MenuSaveRequest;
import com.noonoo.prjtbackend.menu.dto.MenuSearchCondition;

import java.util.List;

public interface MenuService {
    List<MenuDto> findMenusByRole(String roleCode);

    /** 관리자 트리 화면용 전체 메뉴 (USE_YN=Y) */
    List<MenuDto> findAllForTree();

    PageResponse<MenuDto> selectList(MenuSearchCondition condition);

    MenuDto selectDetail(MenuSearchCondition condition);

    int insertData(MenuSaveRequest request);

    int updateData(MenuSaveRequest request);

    int deleteData(MenuSaveRequest request);
}
