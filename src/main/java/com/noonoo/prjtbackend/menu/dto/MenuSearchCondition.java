package com.noonoo.prjtbackend.menu.dto;

import com.noonoo.prjtbackend.common.paging.PageRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class MenuSearchCondition extends PageRequest {

    private Long menuId;
    private String menuCode;
    private String menuName;
    private Long parentMenuId;
    private String useYn;

    public MenuSearchCondition() {
        setSortBy("sortOrder");
        setSortDir("asc");
    }
}
