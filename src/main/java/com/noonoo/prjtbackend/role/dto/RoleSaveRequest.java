package com.noonoo.prjtbackend.role.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleSaveRequest {
    private Long roleId;
    private String roleCode;
    private String roleName;
    private String useYn;
}
