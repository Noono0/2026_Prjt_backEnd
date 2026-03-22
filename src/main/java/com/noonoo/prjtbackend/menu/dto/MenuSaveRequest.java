package com.noonoo.prjtbackend.menu.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuSaveRequest {
    private Long menuId;
    private String menuCode;
    private String menuName;
    private String menuPath;
    private Long parentMenuId;
    private Integer sortOrder;
    private String useYn;
}
