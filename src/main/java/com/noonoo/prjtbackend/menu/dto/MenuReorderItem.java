package com.noonoo.prjtbackend.menu.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 트리 드래그 시 부모/정렬만 일괄 반영 (메뉴코드·이름 변경 없음)
 */
@Getter
@Setter
public class MenuReorderItem {
    private Long menuId;
    /** 루트면 null */
    private Long parentMenuId;
    private Integer sortOrder;
}
