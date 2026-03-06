package com.noonoo.prjtbackend.menu.serviceImpl;

import com.noonoo.prjtbackend.menu.dto.MenuDto;
import com.noonoo.prjtbackend.menu.mapper.MenuMapper;
import com.noonoo.prjtbackend.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuMapper menuMapper;

    @Override
    public List<MenuDto> findMenusByRole(String roleCode) {
        return menuMapper.findMenusByRole(roleCode);
    }
}
