package com.noonoo.prjtbackend.role.mapper;

import java.util.List;

import com.noonoo.prjtbackend.role.dto.RoleDto;
import com.noonoo.prjtbackend.role.dto.RoleSaveRequest;
import com.noonoo.prjtbackend.role.dto.RoleSearchCondition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface RoleMapper {
    List<RoleDto> findAllActiveRoles();

    long selectListCnt(RoleSearchCondition condition);

    List<RoleDto> selectList(RoleSearchCondition condition);

    RoleDto selectDetail(RoleSearchCondition condition);

    long countByRoleCode(@Param("roleCode") String roleCode, @Param("excludeRoleId") Long excludeRoleId);

    int insertData(RoleSaveRequest request);

    int updateData(RoleSaveRequest request);

    int deleteData(RoleSaveRequest request);
}
