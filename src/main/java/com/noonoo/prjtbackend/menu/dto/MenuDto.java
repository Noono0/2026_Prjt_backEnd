package com.noonoo.prjtbackend.menu.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuDto {
    private Long menuId;
    private String menuName;
    private String menuUrl;
    private Long parentMenuId;
    private Integer sortNo;
}
