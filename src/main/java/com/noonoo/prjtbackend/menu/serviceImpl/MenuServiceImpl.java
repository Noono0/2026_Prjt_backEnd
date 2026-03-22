package com.noonoo.prjtbackend.menu.serviceImpl;

import com.noonoo.prjtbackend.common.paging.PageResponse;
import com.noonoo.prjtbackend.common.paging.PagingUtils;
import com.noonoo.prjtbackend.menu.dto.MenuDto;
import com.noonoo.prjtbackend.menu.dto.MenuSaveRequest;
import com.noonoo.prjtbackend.menu.dto.MenuSearchCondition;
import com.noonoo.prjtbackend.menu.mapper.MenuMapper;
import com.noonoo.prjtbackend.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuMapper menuMapper;

    @Override
    public List<MenuDto> findMenusByRole(String roleCode) {
        return menuMapper.findMenusByRole(roleCode);
    }

    @Override
    public List<MenuDto> findAllForTree() {
        return menuMapper.selectAllForTree();
    }

    @Override
    public PageResponse<MenuDto> selectList(MenuSearchCondition condition) {
        long totalCount = menuMapper.selectListCnt(condition);
        List<MenuDto> items = menuMapper.selectList(condition);
        return PagingUtils.toPageResponse(condition, totalCount, items);
    }

    @Override
    public MenuDto selectDetail(MenuSearchCondition condition) {
        return menuMapper.selectDetail(condition);
    }

    @Override
    @Transactional
    public int insertData(MenuSaveRequest request) {
        if (request.getMenuCode() == null || request.getMenuCode().isBlank()) {
            throw new IllegalArgumentException("메뉴 코드는 필수입니다.");
        }
        if (request.getMenuName() == null || request.getMenuName().isBlank()) {
            throw new IllegalArgumentException("메뉴명은 필수입니다.");
        }
        if (menuMapper.countByMenuCode(request.getMenuCode().trim(), null) > 0) {
            throw new IllegalArgumentException("이미 존재하는 메뉴 코드입니다.");
        }
        if (request.getUseYn() == null || request.getUseYn().isBlank()) {
            request.setUseYn("Y");
        }
        if (request.getSortOrder() == null) {
            request.setSortOrder(0);
        }
        request.setMenuCode(request.getMenuCode().trim());
        request.setMenuName(request.getMenuName().trim());
        return menuMapper.insertData(request);
    }

    @Override
    @Transactional
    public int updateData(MenuSaveRequest request) {
        if (request.getMenuId() == null) {
            throw new IllegalArgumentException("menuId가 필요합니다.");
        }
        if (request.getMenuCode() == null || request.getMenuCode().isBlank()) {
            throw new IllegalArgumentException("메뉴 코드는 필수입니다.");
        }
        if (request.getMenuName() == null || request.getMenuName().isBlank()) {
            throw new IllegalArgumentException("메뉴명은 필수입니다.");
        }
        if (menuMapper.countByMenuCode(request.getMenuCode().trim(), request.getMenuId()) > 0) {
            throw new IllegalArgumentException("이미 존재하는 메뉴 코드입니다.");
        }
        if (request.getUseYn() == null || request.getUseYn().isBlank()) {
            request.setUseYn("Y");
        }
        if (request.getSortOrder() == null) {
            request.setSortOrder(0);
        }
        request.setMenuCode(request.getMenuCode().trim());
        request.setMenuName(request.getMenuName().trim());
        return menuMapper.updateData(request);
    }

    @Override
    @Transactional
    public int deleteData(MenuSaveRequest request) {
        if (request.getMenuId() == null) {
            throw new IllegalArgumentException("menuId가 필요합니다.");
        }
        return menuMapper.deleteData(request);
    }
}
