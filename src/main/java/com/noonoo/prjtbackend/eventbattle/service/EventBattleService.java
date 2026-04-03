package com.noonoo.prjtbackend.eventbattle.service;

import com.noonoo.prjtbackend.common.paging.PageResponse;
import com.noonoo.prjtbackend.eventbattle.dto.EventBattleActivityDto;
import com.noonoo.prjtbackend.eventbattle.dto.EventBattleBetRequest;
import com.noonoo.prjtbackend.eventbattle.dto.EventBattleBetRowDto;
import com.noonoo.prjtbackend.eventbattle.dto.EventBattleDto;
import com.noonoo.prjtbackend.eventbattle.dto.EventBattleSaveRequest;
import com.noonoo.prjtbackend.eventbattle.dto.EventBattleSearchCondition;
import com.noonoo.prjtbackend.eventbattle.dto.EventBattleSettleRequest;
import com.noonoo.prjtbackend.eventbattle.dto.EventBattleVoteRequest;

import java.util.List;

public interface EventBattleService {

    EventBattleDto create(EventBattleSaveRequest request);

    PageResponse<EventBattleDto> search(EventBattleSearchCondition condition);

    EventBattleDto get(long eventBattleSeq);

    /** 폴링: 집계 + 최근 베팅 (DB 직조회, Redis는 이후 최적화 후보) */
    EventBattleActivityDto activity(long eventBattleSeq, Long sinceBetSeq, int recentLimit);

    /**
     * SSE 브로드캐스트용: 모든 클라이언트에 동일 JSON을 보내므로
     * myBet / myBetHistory(로그인 사용자 전용)는 포함하지 않습니다.
     */
    EventBattleActivityDto activityForBroadcast(long eventBattleSeq, int recentLimit);

    /** 최근 베팅 추가 페이지 (beforeBetSeq 미만, 최신순) */
    List<EventBattleBetRowDto> recentBetsOlder(long eventBattleSeq, long beforeBetSeq, int limit);

    void placeBet(long eventBattleSeq, EventBattleBetRequest request);
    void vote(long eventBattleSeq, EventBattleVoteRequest request);

    void settle(long eventBattleSeq, EventBattleSettleRequest request);

    /**
     * 이벤트 취소:
     * - 모든 참가자 베팅 포인트 환불
     * - 이벤트를 CANCELLED 상태로 종료
     */
    void cancel(long eventBattleSeq);

    /**
     * 투표 전용 이벤트: 투표만 마감하고 종료(SETTLED, 승리 주제 없음).
     * 이후 {@link #vote(long, EventBattleVoteRequest)} 는 불가합니다.
     */
    void closeVoteOnly(long eventBattleSeq);
}
