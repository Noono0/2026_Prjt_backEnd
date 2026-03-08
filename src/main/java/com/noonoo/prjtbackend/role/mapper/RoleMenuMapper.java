package com.noonoo.prjtbackend.role.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.noonoo.prjtbackend.role.dto.RoleMenuPermissionDto;


@Mapper
public interface RoleMenuMapper {

    List<RoleMenuPermissionDto> findPermissionsByRoleId(@Param("roleId") Long roleId);

    List<RoleMenuPermissionDto> findPermissionsByRoleCode(@Param("roleCode") String roleCode);
}
