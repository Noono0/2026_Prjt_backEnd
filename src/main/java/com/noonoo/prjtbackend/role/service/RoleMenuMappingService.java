package com.noonoo.prjtbackend.role.service;

import com.noonoo.prjtbackend.role.dto.RoleMenuAssignmentDto;
import com.noonoo.prjtbackend.role.dto.RoleMenuMappingQueryRequest;
import com.noonoo.prjtbackend.role.dto.RoleMenuMappingSaveRequest;
import java.util.List;

public interface RoleMenuMappingService {

    List<RoleMenuAssignmentDto> selectAssignments(RoleMenuMappingQueryRequest request);

    void saveAssignments(RoleMenuMappingSaveRequest request);
}
