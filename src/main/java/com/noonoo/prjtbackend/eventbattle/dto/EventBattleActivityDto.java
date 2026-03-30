package com.noonoo.prjtbackend.eventbattle.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 폴링용 스냅샷 (향후 Redis 캐시 후보 — 현재는 DB 직조회).
 */
@Getter
@Builder
public class EventBattleActivityDto {
    private Long eventBattleSeq;
    private String status;
    private List<EventBattleOptionDto> options;
    private Long totalPool;
    /** event_battle_bet 행 수(베팅 실행 횟수) */
    private long betCount;
    /** 서로 다른 회원 수 */
    private long participantCount;
    private Long lastBetSeq;
    private List<EventBattleBetRowDto> recentBets;
    /** 이벤트 내 총 베팅액 상위 (포인트 랭킹) */
    private List<EventBattleBettorRankDto> bettorRanking;
    /** SETTLED일 때만 — 승리 주제 베팅자 중 정산 지급 상위 5명 (지급액 내림차순) */
    @Builder.Default
    private List<EventBattleWinnerPayoutRowDto> winnerPayoutTop5 = new ArrayList<>();
    /** SETTLED일 때만 — 승리측에서 위 5명 제외 인원 수 */
    private int winnerPayoutOtherMemberCount;
    /** SETTLED일 때만 — 승리측에서 위 5명 제외 인원에게 지급된 포인트 합계 */
    private long winnerPayoutOtherTotal;
    /** 로그인 유저가 이미 베팅했으면 1건, 없으면 null */
    private EventBattleMyBetDto myBet;
    /** 로그인 유저 본인 베팅 행(건당 금액, 최신순) */
    private List<EventBattleBetRowDto> myBetHistory;
}
