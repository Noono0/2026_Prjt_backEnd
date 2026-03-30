package com.noonoo.prjtbackend.member;

import com.noonoo.prjtbackend.member.wallet.WalletPointRules;
import org.springframework.util.StringUtils;

/**
 * 포인트 원장 reason_code → 화면용 한글 라벨 (랭킹 사유 표시).
 */
public final class PointRankingReasonLabels {

    private PointRankingReasonLabels() {}

    public static String label(String reasonCode) {
        if (!StringUtils.hasText(reasonCode)) {
            return "기타";
        }
        String c = reasonCode.trim();
        return switch (c) {
            case WalletPointRules.REASON_SIGNUP -> "회원가입";
            case WalletPointRules.REASON_FREE_BOARD_POST -> "자유게시판 글 작성";
            case WalletPointRules.REASON_BOARD_COMMENT_FIRST -> "자유게시판 댓글(첫 댓글)";
            case WalletPointRules.REASON_BOARD_COMMENT_EXTRA -> "자유게시판 댓글(추가)";
            case WalletPointRules.REASON_NOTICE_COMMENT_FIRST -> "공지 댓글(첫 댓글)";
            case WalletPointRules.REASON_NOTICE_COMMENT_EXTRA -> "공지 댓글(추가)";
            case WalletPointRules.REASON_FREE_BOARD_LIKE_MILESTONE -> "자유게시판 추천 보상";
            case WalletPointRules.REASON_FREE_BOARD_POST_BLIND -> "게시글 블라인드(회수·조정)";
            case WalletPointRules.REASON_COMMENT_BLIND -> "댓글 블라인드(회수·조정)";
            case WalletPointRules.REASON_EVENT_BATTLE_BET -> "이벤트 대결 베팅";
            case WalletPointRules.REASON_EVENT_BATTLE_WIN -> "이벤트 대결 승리 정산";
            case WalletPointRules.REASON_EVENT_BATTLE_REFUND -> "이벤트 대결 환급";
            case WalletPointRules.REASON_POINT_GIFT_SENT -> "포인트 선물(보냄)";
            case WalletPointRules.REASON_POINT_GIFT_RECEIVED -> "포인트 선물(받음)";
            case "PURCHASE_IRON" -> "아이언 티켓 구매";
            case "EXCHANGE_IRON_SILVER" -> "아이언→실버 교환";
            case "EXCHANGE_SILVER_GOLD" -> "실버→골드 교환";
            case "EXCHANGE_GOLD_DIAMOND" -> "골드→다이아 교환";
            default -> c;
        };
    }
}
