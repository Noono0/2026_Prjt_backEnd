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

    /** 회원에게 부여된 여러 ROLE_CODE에 대한 메뉴권한 합집합(중복 제거는 AuthorityBuilder에서) */
    List<RoleMenuPermissionDto> findPermissionsByRoleCodes(@Param("roleCodes") List<String> roleCodes);

    List<RoleMenuAssignmentDto> selectAllMenusWithMapping(@Param("roleId") Long roleId);

    int deleteByRoleId(@Param("roleId") Long roleId);

    int insertRoleMenu(RoleMenuInsertParam param);
}
