package com.noonoo.prjtbackend.role.service;

import com.noonoo.prjtbackend.role.dto.RoleDto;
import com.noonoo.prjtbackend.role.dto.RoleSaveRequest;
import com.noonoo.prjtbackend.role.dto.RoleSearchCondition;
import com.noonoo.prjtbackend.common.paging.PageResponse;

import java.util.List;


public interface RoleService {
    List<RoleDto> findAllActiveRoles();

    PageResponse<RoleDto> selectList(RoleSearchCondition condition);

    RoleDto selectDetail(RoleSearchCondition condition);

    int insertData(RoleSaveRequest request);

    int updateData(RoleSaveRequest request);

    int deleteData(RoleSaveRequest request);
}
