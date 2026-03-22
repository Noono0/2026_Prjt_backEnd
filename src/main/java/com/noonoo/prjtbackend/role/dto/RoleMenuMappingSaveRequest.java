package com.noonoo.prjtbackend.role.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleMenuMappingSaveRequest {

    private Long roleId;
    private List<RoleMenuMappingItem> items = new ArrayList<>();

    @Getter
    @Setter
    public static class RoleMenuMappingItem {
        private Long menuId;
        private String canRead;
        private String canCreate;
        private String canUpdate;
        private String canDelete;
    }
}
