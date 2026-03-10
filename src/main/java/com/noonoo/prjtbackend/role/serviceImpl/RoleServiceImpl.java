package com.noonoo.prjtbackend.role.serviceImpl;

import java.util.List;

import com.noonoo.prjtbackend.role.dto.RoleDto;
import com.noonoo.prjtbackend.role.mapper.RoleMapper;
import com.noonoo.prjtbackend.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;

    @Override
    public List<RoleDto> findAllActiveRoles() {
        System.out.println("=============> RoleServiceImpl findAllActiveRoles");
        return roleMapper.findAllActiveRoles();
    }
}
