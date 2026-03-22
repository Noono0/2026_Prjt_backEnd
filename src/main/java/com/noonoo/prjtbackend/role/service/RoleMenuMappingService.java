package com.noonoo.prjtbackend.role.service;

import java.util.List;

import com.noonoo.prjtbackend.role.dto.RoleMenuAssignmentDto;
import com.noonoo.prjtbackend.role.dto.RoleMenuMappingQueryRequest;
import com.noonoo.prjtbackend.role.dto.RoleMenuMappingSaveRequest;

public interface RoleMenuMappingService {

    List<RoleMenuAssignmentDto> selectAssignments(RoleMenuMappingQueryRequest request);

    void saveAssignments(RoleMenuMappingSaveRequest request);
}
