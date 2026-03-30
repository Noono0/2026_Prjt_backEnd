package com.noonoo.prjtbackend.eventbattle.controller;

import com.noonoo.prjtbackend.common.api.ApiResponse;
import com.noonoo.prjtbackend.common.paging.PageResponse;
import com.noonoo.prjtbackend.common.security.MenuAuthorities;
import com.noonoo.prjtbackend.eventbattle.dto.EventBattleActivityDto;
import com.noonoo.prjtbackend.eventbattle.dto.EventBattleBetRowDto;
import com.noonoo.prjtbackend.eventbattle.dto.EventBattleBetRequest;
import com.noonoo.prjtbackend.eventbattle.dto.EventBattleDto;
import com.noonoo.prjtbackend.eventbattle.dto.EventBattleSaveRequest;
import com.noonoo.prjtbackend.eventbattle.dto.EventBattleSearchCondition;
import com.noonoo.prjtbackend.eventbattle.dto.EventBattleSettleRequest;
import com.noonoo.prjtbackend.eventbattle.service.EventBattleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/event-battles")
@RequiredArgsConstructor
public class EventBattleController {

    private final EventBattleService eventBattleService;

    @PostMapping("/search")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.EVENT_BATTLE + "')")
    public ApiResponse<PageResponse<EventBattleDto>> search(@RequestBody(required = false) EventBattleSearchCondition condition) {
        return ApiResponse.ok("목록 조회 완료", eventBattleService.search(condition));
    }

    @GetMapping("/{eventBattleSeq}")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.EVENT_BATTLE + "')")
    public ApiResponse<EventBattleDto> detail(@PathVariable long eventBattleSeq) {
        return ApiResponse.ok("상세 조회 완료", eventBattleService.get(eventBattleSeq));
    }

    /**
     * 폴링용 스냅샷 (DB 직조회 — Redis는 이후 캐시 후보).
     */
    @GetMapping("/{eventBattleSeq}/activity")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.EVENT_BATTLE + "')")
    public ApiResponse<EventBattleActivityDto> activity(
            @PathVariable long eventBattleSeq,
            @RequestParam(required = false) Long sinceBetSeq,
            @RequestParam(required = false, defaultValue = "20") int recentLimit
    ) {
        return ApiResponse.ok("활동 조회 완료", eventBattleService.activity(eventBattleSeq, sinceBetSeq, recentLimit));
    }

    /**
     * 최근 베팅 목록의 다음 페이지 (beforeBetSeq 보다 오래된 행, 최신순).
     */
    @GetMapping("/{eventBattleSeq}/recent-bets")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.EVENT_BATTLE + "')")
    public ApiResponse<List<EventBattleBetRowDto>> recentBetsOlder(
            @PathVariable long eventBattleSeq,
            @RequestParam long beforeBetSeq,
            @RequestParam(required = false, defaultValue = "20") int limit
    ) {
        return ApiResponse.ok("조회 완료", eventBattleService.recentBetsOlder(eventBattleSeq, beforeBetSeq, limit));
    }

    @PostMapping
    @PreAuthorize("@securityExpressions.canCreate('" + MenuAuthorities.EVENT_BATTLE + "')")
    public ApiResponse<EventBattleDto> create(@RequestBody EventBattleSaveRequest request) {
        return ApiResponse.ok("이벤트가 등록되었습니다.", eventBattleService.create(request));
    }

    @PostMapping("/{eventBattleSeq}/bets")
    @PreAuthorize("@securityExpressions.isAuthenticatedOrPermitAll()")
    public ApiResponse<String> bet(@PathVariable long eventBattleSeq, @RequestBody EventBattleBetRequest request) {
        eventBattleService.placeBet(eventBattleSeq, request);
        return ApiResponse.ok("베팅이 반영되었습니다.", "OK");
    }

    @PostMapping("/{eventBattleSeq}/settle")
    @PreAuthorize("@securityExpressions.isAuthenticatedOrPermitAll()")
    public ApiResponse<String> settle(@PathVariable long eventBattleSeq, @RequestBody EventBattleSettleRequest request) {
        eventBattleService.settle(eventBattleSeq, request);
        return ApiResponse.ok("정산이 완료되었습니다.", "OK");
    }
}
