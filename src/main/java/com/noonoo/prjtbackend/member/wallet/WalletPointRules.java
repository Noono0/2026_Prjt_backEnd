package com.noonoo.prjtbackend.member.wallet;

/**
 * 활동 적립 포인트 규칙 (고정 상수).
 */
public final class WalletPointRules {

    private WalletPointRules() {}

    /** 회원가입 시 기본 지급 */
    public static final long SIGNUP_BONUS = 100L;

    /** 자유게시판 글 작성 (관리 정책 화면 기본값; 실제 지급은 app.wallet.free-board-post-points) */
    public static final long FREE_BOARD_POST = 100L;

    /** 자유게시판·공지: 해당 게시글에 대한 첫 댓글 */
    public static final long COMMENT_FIRST_ON_POST = 10L;

    /** 자유게시판·공지: 첫 댓글 이후 같은 게시글에서 추가로 적립 가능한 상한(합계) */
    public static final long COMMENT_EXTRA_CAP_PER_POST = 5L;

    public static final String REASON_SIGNUP = "POINT_SIGNUP";
    public static final String REASON_FREE_BOARD_POST = "POINT_FREE_BOARD_POST";
    public static final String REASON_BOARD_COMMENT_FIRST = "POINT_BOARD_COMMENT_FIRST";
    public static final String REASON_BOARD_COMMENT_EXTRA = "POINT_BOARD_COMMENT_EXTRA";
    public static final String REASON_NOTICE_COMMENT_FIRST = "POINT_NOTICE_COMMENT_FIRST";
    public static final String REASON_NOTICE_COMMENT_EXTRA = "POINT_NOTICE_COMMENT_EXTRA";

    /** 자유게시판 글 추천 수가 정책 임계값 이상일 때 작성자 1회 보상 */
    public static final String REASON_FREE_BOARD_LIKE_MILESTONE = "POINT_FREE_BOARD_LIKE_MILESTONE";

    /** 자유게시판: 신고 누적으로 블라인드 시 글 작성 적립 포인트 회수 */
    public static final String REASON_FREE_BOARD_POST_BLIND = "POINT_FREE_BOARD_POST_BLIND";

    /** 자유게시판·공지: 댓글 신고 누적으로 블라인드 시 댓글 적립 포인트 회수(app.wallet.free-board-post-points 와 동일 금액) */
    public static final String REASON_COMMENT_BLIND = "POINT_COMMENT_BLIND";

    /** 다른 회원에게 포인트 선물(보낸 쪽 원장) */
    public static final String REASON_POINT_GIFT_SENT = "POINT_GIFT_SENT";

    /** 다른 회원에게 포인트 선물(받는 쪽 원장) */
    public static final String REASON_POINT_GIFT_RECEIVED = "POINT_GIFT_RECEIVED";

    /** 이벤트 대결에 포인트 베팅(차감) */
    public static final String REASON_EVENT_BATTLE_BET = "EVENT_BATTLE_BET";

    /** 이벤트 대결 승리 정산(지급) */
    public static final String REASON_EVENT_BATTLE_WIN = "EVENT_BATTLE_WIN";

    /** 이벤트 무승부/예외 시 베팅 환급 */
    public static final String REASON_EVENT_BATTLE_REFUND = "EVENT_BATTLE_REFUND";
}
