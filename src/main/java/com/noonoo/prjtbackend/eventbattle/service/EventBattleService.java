package com.noonoo.prjtbackend.eventbattle.service;

import com.noonoo.prjtbackend.common.paging.PageResponse;
import com.noonoo.prjtbackend.eventbattle.dto.EventBattleActivityDto;
import com.noonoo.prjtbackend.eventbattle.dto.EventBattleBetRequest;
import com.noonoo.prjtbackend.eventbattle.dto.EventBattleBetRowDto;
import com.noonoo.prjtbackend.eventbattle.dto.EventBattleDto;
import com.noonoo.prjtbackend.eventbattle.dto.EventBattleSaveRequest;
import com.noonoo.prjtbackend.eventbattle.dto.EventBattleSearchCondition;
import com.noonoo.prjtbackend.eventbattle.dto.EventBattleSettleRequest;

import java.util.List;

public interface EventBattleService {

    EventBattleDto create(EventBattleSaveRequest request);

    PageResponse<EventBattleDto> search(EventBattleSearchCondition condition);

    EventBattleDto get(long eventBattleSeq);

    /** 폴링: 집계 + 최근 베팅 (DB 직조회, Redis는 이후 최적화 후보) */
    EventBattleActivityDto activity(long eventBattleSeq, Long sinceBetSeq, int recentLimit);

    /** 최근 베팅 추가 페이지 (beforeBetSeq 미만, 최신순) */
    List<EventBattleBetRowDto> recentBetsOlder(long eventBattleSeq, long beforeBetSeq, int limit);

    void placeBet(long eventBattleSeq, EventBattleBetRequest request);

    void settle(long eventBattleSeq, EventBattleSettleRequest request);
}
