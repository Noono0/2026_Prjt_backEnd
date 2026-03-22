package com.noonoo.prjtbackend.role.dto;

import com.noonoo.prjtbackend.common.paging.PageRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class RoleSearchCondition extends PageRequest {

    private Long roleId;
    private String roleCode;
    private String roleName;
    private String useYn;

    public RoleSearchCondition() {
        setSortBy("roleId");
        setSortDir("desc");
    }
}
