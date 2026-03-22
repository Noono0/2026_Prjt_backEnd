package com.noonoo.prjtbackend.role.serviceImpl;

import java.util.List;

import com.noonoo.prjtbackend.role.dto.RoleDto;
import com.noonoo.prjtbackend.role.dto.RoleSaveRequest;
import com.noonoo.prjtbackend.role.dto.RoleSearchCondition;
import com.noonoo.prjtbackend.role.mapper.RoleMapper;
import com.noonoo.prjtbackend.role.service.RoleService;
import com.noonoo.prjtbackend.common.paging.PageResponse;
import com.noonoo.prjtbackend.common.paging.PagingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleMapper roleMapper;

    @Override
    public List<RoleDto> findAllActiveRoles() {
        System.out.println("=============> RoleServiceImpl findAllActiveRoles");
        return roleMapper.findAllActiveRoles();
    }

    @Override
    public PageResponse<RoleDto> selectList(RoleSearchCondition condition) {
        long totalCount = roleMapper.selectListCnt(condition);
        List<RoleDto> items = roleMapper.selectList(condition);
        return PagingUtils.toPageResponse(condition, totalCount, items);
    }

    @Override
    public RoleDto selectDetail(RoleSearchCondition condition) {
        return roleMapper.selectDetail(condition);
    }

    @Override
    @Transactional
    public int insertData(RoleSaveRequest request) {
        if (request.getRoleCode() == null || request.getRoleCode().isBlank()) {
            throw new IllegalArgumentException("권한 코드는 필수입니다.");
        }
        if (request.getRoleName() == null || request.getRoleName().isBlank()) {
            throw new IllegalArgumentException("권한명은 필수입니다.");
        }
        if (roleMapper.countByRoleCode(request.getRoleCode().trim(), null) > 0) {
            throw new IllegalArgumentException("이미 존재하는 권한 코드입니다.");
        }
        if (request.getUseYn() == null || request.getUseYn().isBlank()) {
            request.setUseYn("Y");
        }
        request.setRoleCode(request.getRoleCode().trim());
        request.setRoleName(request.getRoleName().trim());
        return roleMapper.insertData(request);
    }

    @Override
    @Transactional
    public int updateData(RoleSaveRequest request) {
        if (request.getRoleId() == null) {
            throw new IllegalArgumentException("roleId가 필요합니다.");
        }
        if (request.getRoleCode() == null || request.getRoleCode().isBlank()) {
            throw new IllegalArgumentException("권한 코드는 필수입니다.");
        }
        if (request.getRoleName() == null || request.getRoleName().isBlank()) {
            throw new IllegalArgumentException("권한명은 필수입니다.");
        }
        if (roleMapper.countByRoleCode(request.getRoleCode().trim(), request.getRoleId()) > 0) {
            throw new IllegalArgumentException("이미 존재하는 권한 코드입니다.");
        }
        if (request.getUseYn() == null || request.getUseYn().isBlank()) {
            request.setUseYn("Y");
        }
        request.setRoleCode(request.getRoleCode().trim());
        request.setRoleName(request.getRoleName().trim());
        return roleMapper.updateData(request);
    }

    @Override
    @Transactional
    public int deleteData(RoleSaveRequest request) {
        if (request.getRoleId() == null) {
            throw new IllegalArgumentException("roleId가 필요합니다.");
        }
        return roleMapper.deleteData(request);
    }
}
