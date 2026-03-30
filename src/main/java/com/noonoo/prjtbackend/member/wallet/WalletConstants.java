package com.noonoo.prjtbackend.member.wallet;

/**
 * 포인트·티켓 교환 규칙 (고정).
 */
public final class WalletConstants {

    private WalletConstants() {}

    /** 포인트 1000개 = 아이언 티켓 1장 */
    public static final long POINTS_PER_IRON = 1000L;
    /** 아이언 10장 = 실버 1장 */
    public static final int IRON_PER_SILVER = 10;
    /** 실버 10장 = 골드 1장 */
    public static final int SILVER_PER_GOLD = 10;
    /** 골드 10장 = 다이아 1장 */
    public static final int GOLD_PER_DIAMOND = 10;
}
