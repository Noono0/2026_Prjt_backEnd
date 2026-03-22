package com.noonoo.prjtbackend.role.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.noonoo.prjtbackend.role.dto.RoleMenuAssignmentDto;
import com.noonoo.prjtbackend.role.dto.RoleMenuInsertParam;
import com.noonoo.prjtbackend.role.dto.RoleMenuMappingQueryRequest;
import com.noonoo.prjtbackend.role.dto.RoleMenuMappingSaveRequest;
import com.noonoo.prjtbackend.role.dto.RoleMenuMappingSaveRequest.RoleMenuMappingItem;
import com.noonoo.prjtbackend.role.dto.RoleDto;
import com.noonoo.prjtbackend.role.dto.RoleSearchCondition;
import com.noonoo.prjtbackend.role.mapper.RoleMapper;
import com.noonoo.prjtbackend.role.mapper.RoleMenuMapper;
import com.noonoo.prjtbackend.role.service.RoleMenuMappingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleMenuMappingServiceImpl implements RoleMenuMappingService {

    private final RoleMenuMapper roleMenuMapper;
    private final RoleMapper roleMapper;

    @Override
    public List<RoleMenuAssignmentDto> selectAssignments(RoleMenuMappingQueryRequest request) {
        Long roleId = request != null ? request.getRoleId() : null;
        if (roleId == null) {
            throw new IllegalArgumentException("roleId가 필요합니다.");
        }
        validateRoleExists(roleId);
        return roleMenuMapper.selectAllMenusWithMapping(roleId);
    }

    @Override
    @Transactional
    public void saveAssignments(RoleMenuMappingSaveRequest request) {
        if (request == null || request.getRoleId() == null) {
            throw new IllegalArgumentException("roleId가 필요합니다.");
        }
        Long roleId = request.getRoleId();
        validateRoleExists(roleId);

        roleMenuMapper.deleteByRoleId(roleId);

        if (request.getItems() == null) {
            return;
        }

        for (RoleMenuMappingItem item : request.getItems()) {
            if (item == null || item.getMenuId() == null) {
                continue;
            }
            if (!hasAnyPermission(item)) {
                continue;
            }
            RoleMenuInsertParam param = new RoleMenuInsertParam();
            param.setRoleId(roleId);
            param.setMenuId(item.getMenuId());
            param.setCanRead(yn(item.getCanRead()));
            param.setCanCreate(yn(item.getCanCreate()));
            param.setCanUpdate(yn(item.getCanUpdate()));
            param.setCanDelete(yn(item.getCanDelete()));
            roleMenuMapper.insertRoleMenu(param);
        }
    }

    private void validateRoleExists(Long roleId) {
        RoleSearchCondition c = new RoleSearchCondition();
        c.setRoleId(roleId);
        RoleDto role = roleMapper.selectDetail(c);
        if (role == null) {
            throw new IllegalArgumentException("존재하지 않는 권한(역할)입니다.");
        }
    }

    private static boolean hasAnyPermission(RoleMenuMappingItem item) {
        return "Y".equalsIgnoreCase(item.getCanRead())
                || "Y".equalsIgnoreCase(item.getCanCreate())
                || "Y".equalsIgnoreCase(item.getCanUpdate())
                || "Y".equalsIgnoreCase(item.getCanDelete());
    }

    private static String yn(String v) {
        return "Y".equalsIgnoreCase(v) ? "Y" : "N";
    }
}
