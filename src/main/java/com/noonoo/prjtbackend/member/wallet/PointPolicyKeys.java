package com.noonoo.prjtbackend.member.wallet;

/**
 * point_policy_setting.policy_key 값과 동일해야 함.
 */
public final class PointPolicyKeys {

    private PointPolicyKeys() {}

    public static final String SIGNUP = "SIGNUP";
    public static final String FREE_BOARD_POST = "FREE_BOARD_POST";
    public static final String BOARD_COMMENT_FIRST = "BOARD_COMMENT_FIRST";
    /** 게시글당 첫 댓글 이후 추가 적립 합산 상한 */
    public static final String BOARD_COMMENT_EXTRA = "BOARD_COMMENT_EXTRA";
    public static final String NOTICE_COMMENT_FIRST = "NOTICE_COMMENT_FIRST";
    public static final String NOTICE_COMMENT_EXTRA = "NOTICE_COMMENT_EXTRA";
    public static final String FREE_BOARD_LIKE = "FREE_BOARD_LIKE";
}
