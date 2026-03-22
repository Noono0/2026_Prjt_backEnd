package com.noonoo.prjtbackend.role.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 역할별 메뉴 매핑 화면용: 전체 메뉴 + 해당 역할의 ROLE_MENU 권한
 */
@Getter
@Setter
public class RoleMenuAssignmentDto {

    private Long menuId;
    private String menuCode;
    private String menuName;
    private String menuPath;
    private Long parentMenuId;
    private Integer sortOrder;

    /** ROLE_MENU 행이 있으면 값 존재 */
    private Long roleMenuId;

    private String canRead;
    private String canCreate;
    private String canUpdate;
    private String canDelete;
}
