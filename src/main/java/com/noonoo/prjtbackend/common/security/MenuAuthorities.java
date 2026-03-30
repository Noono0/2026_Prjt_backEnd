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
    public static final String BOARD = "BOARD";
    public static final String NOTICE_BOARD = "NOTICE_BOARD";
    public static final String SITE_POPUP = "SITE_POPUP";
    /** 비속어·광고 필터 단어 관리 */
    public static final String CONTENT_FILTER = "CONTENT_FILTER";
    /** 일정·생일 달력 (자유게시판과 별도) */
    public static final String CALENDAR_SCHEDULE = "CALENDAR_SCHEDULE";
    /** 포인트 획득 랭킹 (일·주·월) */
    public static final String POINT_RANKING = "POINT_RANKING";
    /** 포인트 정책 (자유게시판 추천 마일스톤 등) */
    public static final String POINT_POLICY = "POINT_POLICY";
    /** 블랙리스트 제보 게시판 */
    public static final String BLACKLIST_REPORT = "BLACKLIST_REPORT";
    /** 좌·우 대결 포인트 이벤트 (관리자·스트리머 생성, 전원 참여) */
    public static final String EVENT_BATTLE = "EVENT_BATTLE";
}
