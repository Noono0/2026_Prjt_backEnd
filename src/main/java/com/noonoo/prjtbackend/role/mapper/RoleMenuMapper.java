package com.noonoo.prjtbackend.role.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.noonoo.prjtbackend.role.dto.RoleMenuAssignmentDto;
import com.noonoo.prjtbackend.role.dto.RoleMenuInsertParam;
import com.noonoo.prjtbackend.role.dto.RoleMenuPermissionDto;


@Mapper
public interface RoleMenuMapper {

    List<RoleMenuPermissionDto> findPermissionsByRoleId(@Param("roleId") Long roleId);

    List<RoleMenuPermissionDto> findPermissionsByRoleCode(@Param("roleCode") String roleCode);

    List<RoleMenuAssignmentDto> selectAllMenusWithMapping(@Param("roleId") Long roleId);

    int deleteByRoleId(@Param("roleId") Long roleId);

    int insertRoleMenu(RoleMenuInsertParam param);
}
