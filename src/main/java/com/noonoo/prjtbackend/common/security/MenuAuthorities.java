package com.noonoo.prjtbackend.common.security;

/**
 * DB MENU.MENU_CODE 와 AuthorityBuilder 규칙({MENU_CODE}_{READ|CREATE|UPDATE|DELETE})에 맞춘 상수.
 * 역할–메뉴 매핑 화면의 메뉴 코드와 동일해야 합니다.
 */
public final class MenuAuthorities {

    private MenuAuthorities() {}

    public static final String MEMBER = "MEMBER";
    public static final String MENU = "MENU";
    public static final String ROLE = "ROLE";
    public static final String CODE_GROUP = "CODE_GROUP";
    public static final String CODE_DETAIL = "CODE_DETAIL";
}
