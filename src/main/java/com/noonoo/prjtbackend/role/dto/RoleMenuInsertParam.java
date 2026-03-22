package com.noonoo.prjtbackend.role.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleMenuInsertParam {

    private Long roleId;
    private Long menuId;
    private String canRead;
    private String canCreate;
    private String canUpdate;
    private String canDelete;
}
