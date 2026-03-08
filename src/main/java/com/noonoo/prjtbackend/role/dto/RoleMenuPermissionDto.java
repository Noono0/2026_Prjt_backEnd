package com.noonoo.prjtbackend.role.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleMenuPermissionDto {
    private String roleCode;
    private String menuCode;
    private String menuName;
    private String menuPath;
    private String canRead;
    private String canCreate;
    private String canUpdate;
    private String canDelete;
}
