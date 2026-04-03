package com.noonoo.prjtbackend.eventbattle.serviceImpl;

import com.noonoo.prjtbackend.common.config.RequestContext;
import com.noonoo.prjtbackend.common.paging.PageResponse;
import com.noonoo.prjtbackend.common.paging.PagingUtils;
import com.noonoo.prjtbackend.common.security.MenuAuthorities;
import com.noonoo.prjtbackend.common.security.SecurityExpressions;
import com.noonoo.prjtbackend.eventbattle.dto.EventBattleActivityDto;
import com.noonoo.prjtbackend.eventbattle.dto.EventBattleBettorRankDto;
import com.noonoo.prjtbackend.eventbattle.dto.EventBattleStakeMemberRow;
import com.noonoo.prjtbackend.eventbattle.dto.EventBattleWinnerPayoutRowDto;
import com.noonoo.prjtbackend.eventbattle.dto.EventBattleBetRequest;
import com.noonoo.prjtbackend.eventbattle.dto.EventBattleBetRowDto;
import com.noonoo.prjtbackend.eventbattle.dto.EventBattleDto;
import com.noonoo.prjtbackend.eventbattle.dto.EventBattleMyBetDto;
import com.noonoo.prjtbackend.eventbattle.dto.EventBattleOptionDto;
import com.noonoo.prjtbackend.eventbattle.dto.EventBattleSaveRequest;
import com.noonoo.prjtbackend.eventbattle.dto.EventBattleSearchCondition;
import com.noonoo.prjtbackend.eventbattle.dto.EventBattleSettleRequest;
import com.noonoo.prjtbackend.eventbattle.dto.EventBattleVoteRequest;
import com.noonoo.prjtbackend.eventbattle.dto.MemberStakeRow;
import com.noonoo.prjtbackend.eventbattle.mapper.EventBattleMapper;
import com.noonoo.prjtbackend.eventbattle.service.EventBattleService;
import com.noonoo.prjtbackend.eventbattle.sse.EventBattleActivitySseBroadcaster;
import com.noonoo.prjtbackend.member.dto.MemberWalletLedgerDto;
import com.noonoo.prjtbackend.member.mapper.MemberWalletMapper;
import com.noonoo.prjtbackend.member.wallet.WalletPointRules;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventBattleServiceImpl implements EventBattleService {

    private static final int ACTIVITY_RECENT_DEFAULT = 20;
    private static final int BETTOR_RANKING_TOP = 10;
    private static final int MY_BET_HISTORY_LIMIT = 5;
    private static final int MIN_OPTIONS = 2;

    private final EventBattleMapper eventBattleMapper;
    private final MemberWalletMapper memberWalletMapper;
    private final SecurityExpressions securityExpressions;
    private final EventBattleActivitySseBroadcaster sseBroadcaster;

    @Override
    @Transactional
    public EventBattleDto create(EventBattleSaveRequest request) {
        if (request == null || !StringUtils.hasText(request.getTitle())) {
            throw new IllegalArgumentException("이벤트 제목을 입력해 주세요.");
        }
        List<String> labels = normalizeOptionLabels(request.getOptionLabels());
        if (labels.size() < MIN_OPTIONS) {
            throw new IllegalArgumentException("주제는 " + MIN_OPTIONS + "개 이상 입력해 주세요.");
        }
        long creator = requireLoginMemberSeq();
        String loginId = RequestContext.getLoginMemberId();
        String ip = RequestContext.getClientIp();
        request.setTitle(request.getTitle().trim());
        request.setVoteLimitPerMember(request.getVoteLimitPerMember() == null || request.getVoteLimitPerMember() < 1 ? 1 : request.getVoteLimitPerMember());
        request.setVoteOnlyYn(Boolean.TRUE.equals(request.getVoteOnly()) ? "Y" : "N");
        request.setCreatorMemberSeq(creator);
        request.setCreateId(StringUtils.hasText(loginId) ? loginId : "SYSTEM");
        request.setCreateIp(StringUtils.hasText(ip) ? ip : "0.0.0.0");
        eventBattleMapper.insertEvent(request);
        Long seq = request.getEventBattleSeq();
        if (seq == null) {
            throw new IllegalStateException("이벤트 저장에 실패했습니다.");
        }
        int order = 1;
        for (String lab : labels) {
            eventBattleMapper.insertOption(seq, order++, lab);
        }
        return get(seq);
    }

    private static List<String> normalizeOptionLabels(List<String> raw) {
        if (raw == null) {
            return List.of();
        }
        return raw.stream()
                .filter(s -> s != null && StringUtils.hasText(s.trim()))
                .map(s -> s.trim())
                .collect(Collectors.toList());
    }

    @Override
    public PageResponse<EventBattleDto> search(EventBattleSearchCondition condition) {
        if (condition == null) {
            condition = new EventBattleSearchCondition();
        }
        long total = eventBattleMapper.findEventsCnt(condition);
        List<EventBattleDto> items = eventBattleMapper.findEvents(condition);
        return PagingUtils.toPageResponse(condition, total, items);
    }

    @Override
    public EventBattleDto get(long eventBattleSeq) {
        EventBattleDto dto = eventBattleMapper.selectById(eventBattleSeq);
        if (dto == null) {
            throw new IllegalArgumentException("이벤트를 찾을 수 없습니다.");
        }
        List<EventBattleOptionDto> opts = eventBattleMapper.selectOptionsByEventSeq(eventBattleSeq);
        dto.setOptions(opts != null ? opts : new ArrayList<>());
        return dto;
    }

    @Override
    public EventBattleActivityDto activity(long eventBattleSeq, Long sinceBetSeq, int recentLimit) {
        return buildActivity(eventBattleSeq, sinceBetSeq, recentLimit, true);
    }

    @Override
    public EventBattleActivityDto activityForBroadcast(long eventBattleSeq, int recentLimit) {
        return buildActivity(eventBattleSeq, null, recentLimit, false);
    }

    private EventBattleActivityDto buildActivity(
            long eventBattleSeq,
            Long sinceBetSeq,
            int recentLimit,
            boolean includeViewerBets
    ) {
        EventBattleDto e = get(eventBattleSeq);
        int lim = recentLimit > 0 && recentLimit <= 200 ? recentLimit : ACTIVITY_RECENT_DEFAULT;
        List<EventBattleBetRowDto> rows = eventBattleMapper.selectRecentBets(eventBattleSeq, sinceBetSeq, lim);
        /* SQL: bet_seq DESC → 최신이 위로 */
        List<EventBattleBettorRankDto> ranking = eventBattleMapper.selectBettorRanking(eventBattleSeq, BETTOR_RANKING_TOP);
        if (ranking != null) {
            for (int i = 0; i < ranking.size(); i++) {
                ranking.get(i).setRank(i + 1);
            }
        }
        long betCount = eventBattleMapper.countBets(eventBattleSeq);
        long participantCount = eventBattleMapper.countDistinctBetMembers(eventBattleSeq);
        Long lastSeq = eventBattleMapper.maxBetSeq(eventBattleSeq);
        List<EventBattleOptionDto> opts = e.getOptions() != null ? e.getOptions() : List.of();
        long totalPool = opts.stream().mapToLong(o -> o.getPointsTotal() != null ? o.getPointsTotal() : 0L).sum();

        EventBattleMyBetDto myBet = null;
        List<EventBattleBetRowDto> myBetHistory = new ArrayList<>();
        List<Long> myVoteOptionSeqs = new ArrayList<>();
        if (includeViewerBets) {
            Long login = RequestContext.getLoginMemberSeq();
            if (login != null) {
                myBet = eventBattleMapper.selectMyBet(eventBattleSeq, login);
                myVoteOptionSeqs = eventBattleMapper.selectMyVoteOptionSeqs(eventBattleSeq, login);
                List<EventBattleBetRowDto> mine = eventBattleMapper.selectMyBetsForEvent(
                        eventBattleSeq, login, MY_BET_HISTORY_LIMIT);
                if (mine != null) {
                    myBetHistory = mine;
                }
            }
        }

        WinnerPayoutCelebrationSplit winnerSplit = WinnerPayoutCelebrationSplit.empty();
        if ("SETTLED".equals(e.getStatus())
                && e.getWinnerOptionSeq() != null
                && e.getWinnerOptionSeq() > 0) {
            winnerSplit = computeWinnerPayoutCelebrationSplit(eventBattleSeq, e.getWinnerOptionSeq(), totalPool);
        }

        return EventBattleActivityDto.builder()
                .eventBattleSeq(eventBattleSeq)
                .status(e.getStatus())
                .options(opts)
                .totalPool(totalPool)
                .betCount(betCount)
                .participantCount(participantCount)
                .lastBetSeq(lastSeq != null ? lastSeq : 0L)
                .recentBets(rows)
                .bettorRanking(ranking != null ? ranking : new ArrayList<>())
                .winnerPayoutTop5(winnerSplit.top5())
                .winnerPayoutOtherMemberCount(winnerSplit.otherMemberCount())
                .winnerPayoutOtherTotal(winnerSplit.otherPayoutTotal())
                .myBet(myBet)
                .myBetHistory(myBetHistory)
                .myVoteOptionSeqs(myVoteOptionSeqs)
                .build();
    }

    /**
     * 정산(settle)과 동일한 비례 배분 + 잔액 round-robin으로 승리측 지급액을 계산한다 (조회 전용).
     */
    private WinnerPayoutCelebrationSplit computeWinnerPayoutCelebrationSplit(
            long eventBattleSeq, long winnerOptionSeq, long totalPool) {
        if (totalPool <= 0) {
            return WinnerPayoutCelebrationSplit.empty();
        }
        List<EventBattleStakeMemberRow> rows = eventBattleMapper.selectStakesByOptionWithMember(
                eventBattleSeq, winnerOptionSeq);
        if (rows == null || rows.isEmpty()) {
            return WinnerPayoutCelebrationSplit.empty();
        }
        long winSum = rows.stream()
                .mapToLong(r -> r.getStakeAmount() != null ? r.getStakeAmount() : 0L)
                .sum();
        if (winSum <= 0) {
            return WinnerPayoutCelebrationSplit.empty();
        }

        List<EventBattleStakeMemberRow> sorted = new ArrayList<>(rows);
        sorted.sort(Comparator.comparing(EventBattleStakeMemberRow::getMemberSeq));

        int n = sorted.size();
        long[] payouts = new long[n];
        long distributed = 0;
        for (int i = 0; i < n; i++) {
            long stake = sorted.get(i).getStakeAmount() != null ? sorted.get(i).getStakeAmount() : 0L;
            long share = (totalPool * stake) / winSum;
            payouts[i] = share;
            distributed += share;
        }
        long remainder = totalPool - distributed;
        int rr = 0;
        while (remainder > 0) {
            payouts[rr % n]++;
            rr++;
            remainder--;
        }

        List<EventBattleWinnerPayoutRowDto> enriched = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            EventBattleStakeMemberRow sr = sorted.get(i);
            enriched.add(EventBattleWinnerPayoutRowDto.builder()
                    .memberDisplayName(sr.getMemberDisplayName())
                    .stakePoints(sr.getStakeAmount())
                    .payoutPoints(payouts[i])
                    .build());
        }
        enriched.sort(Comparator.comparingLong(
                (EventBattleWinnerPayoutRowDto x) -> x.getPayoutPoints() != null ? x.getPayoutPoints() : 0L
        ).reversed());
        for (int i = 0; i < enriched.size(); i++) {
            enriched.get(i).setRank(i + 1);
        }

        List<EventBattleWinnerPayoutRowDto> top5 = enriched.size() <= 5
                ? new ArrayList<>(enriched)
                : new ArrayList<>(enriched.subList(0, 5));
        long otherTotal = 0L;
        for (int i = 5; i < enriched.size(); i++) {
            Long p = enriched.get(i).getPayoutPoints();
            otherTotal += p != null ? p : 0L;
        }
        int otherCount = Math.max(0, enriched.size() - 5);
        return new WinnerPayoutCelebrationSplit(top5, otherCount, otherTotal);
    }

    private record WinnerPayoutCelebrationSplit(
            List<EventBattleWinnerPayoutRowDto> top5,
            int otherMemberCount,
            long otherPayoutTotal
    ) {
        private static WinnerPayoutCelebrationSplit empty() {
            return new WinnerPayoutCelebrationSplit(new ArrayList<>(), 0, 0L);
        }
    }

    @Override
    public List<EventBattleBetRowDto> recentBetsOlder(long eventBattleSeq, long beforeBetSeq, int limit) {
        get(eventBattleSeq);
        int lim = limit > 0 && limit <= 200 ? limit : ACTIVITY_RECENT_DEFAULT;
        if (beforeBetSeq < 1) {
            throw new IllegalArgumentException("beforeBetSeq 가 올바르지 않습니다.");
        }
        List<EventBattleBetRowDto> rows = eventBattleMapper.selectRecentBetsBefore(eventBattleSeq, beforeBetSeq, lim);
        return rows != null ? rows : new ArrayList<>();
    }

    @Override
    @Transactional
    public void placeBet(long eventBattleSeq, EventBattleBetRequest request) {
        long memberSeq = requireLoginMemberSeq();
        if (request == null || request.getPoints() == null || request.getPoints() < 1) {
            throw new IllegalArgumentException("베팅 포인트는 1 이상이어야 합니다.");
        }
        if (request.getEventBattleOptionSeq() == null) {
            throw new IllegalArgumentException("주제를 선택해 주세요.");
        }
        long optionSeq = request.getEventBattleOptionSeq();
        long pts = request.getPoints();

        EventBattleDto e = get(eventBattleSeq);
        if ("Y".equals(e.getVoteOnlyYn())) {
            throw new IllegalStateException("이 이벤트는 투표 전용입니다. 포인트 베팅은 할 수 없습니다.");
        }
        if (!"OPEN".equals(e.getStatus())) {
            throw new IllegalStateException("베팅이 마감된 이벤트입니다.");
        }
        EventBattleOptionDto opt = eventBattleMapper.selectOptionById(optionSeq);
        if (opt == null || opt.getEventBattleSeq() == null || opt.getEventBattleSeq() != eventBattleSeq) {
            throw new IllegalArgumentException("유효하지 않은 주제입니다.");
        }

        EventBattleMyBetDto existing = eventBattleMapper.selectMyBet(eventBattleSeq, memberSeq);
        if (existing != null && existing.getEventBattleOptionSeq() != null
                && !existing.getEventBattleOptionSeq().equals(optionSeq)) {
            throw new IllegalStateException("이미 다른 주제에 베팅했습니다. 같은 주제에만 추가 베팅할 수 있습니다.");
        }

        ensureWallet(memberSeq);
        int sub = memberWalletMapper.subtractPointsIfEnough(memberSeq, pts);
        if (sub == 0) {
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }
        insertLedger(
                memberSeq,
                WalletPointRules.REASON_EVENT_BATTLE_BET,
                String.format("이벤트 대결 베팅 #%d (%s %dP)", eventBattleSeq, opt.getLabel(), pts),
                -pts
        );

        eventBattleMapper.insertBet(eventBattleSeq, optionSeq, memberSeq, pts);
        int n = eventBattleMapper.incrementOptionPoints(optionSeq, pts);
        if (n == 0) {
            throw new IllegalStateException("베팅 반영에 실패했습니다. 이벤트 상태를 확인해 주세요.");
        }

        // 베팅 반영 후 activity 변경을 SSE로 푸시
        sseBroadcaster.broadcastActivity(eventBattleSeq);
    }

    @Override
    @Transactional
    public void vote(long eventBattleSeq, EventBattleVoteRequest request) {
        long memberSeq = requireLoginMemberSeq();
        EventBattleDto e = get(eventBattleSeq);
        if (!"Y".equals(e.getVoteOnlyYn())) {
            throw new IllegalStateException("이 이벤트는 베팅 방식입니다. 투표는 투표 전용 이벤트에서만 가능합니다.");
        }
        if (!"OPEN".equals(e.getStatus())) {
            throw new IllegalStateException("투표가 마감된 이벤트입니다.");
        }
        int limit = e.getVoteLimitPerMember() == null || e.getVoteLimitPerMember() < 1 ? 1 : e.getVoteLimitPerMember();
        List<Long> raw = request != null && request.getOptionSeqs() != null ? request.getOptionSeqs() : List.of();
        List<Long> picks = new ArrayList<>(new LinkedHashSet<>(raw.stream().filter(v -> v != null && v > 0).toList()));
        if (picks.isEmpty()) {
            throw new IllegalArgumentException("최소 1개 주제를 선택해 주세요.");
        }
        if (picks.size() > limit) {
            throw new IllegalArgumentException("투표권은 최대 " + limit + "개까지 선택할 수 있습니다.");
        }
        for (Long optionSeq : picks) {
            EventBattleOptionDto opt = eventBattleMapper.selectOptionById(optionSeq);
            if (opt == null || opt.getEventBattleSeq() == null || opt.getEventBattleSeq() != eventBattleSeq) {
                throw new IllegalArgumentException("이벤트에 속한 유효한 주제를 선택해 주세요.");
            }
        }
        eventBattleMapper.deleteMyVotes(eventBattleSeq, memberSeq);
        for (Long optionSeq : picks) {
            eventBattleMapper.insertVote(eventBattleSeq, optionSeq, memberSeq);
        }
        sseBroadcaster.broadcastActivity(eventBattleSeq);
    }

    @Override
    @Transactional
    public void settle(long eventBattleSeq, EventBattleSettleRequest request) {
        if (request == null || request.getWinnerOptionSeq() == null) {
            throw new IllegalArgumentException("승리 주제를 선택해 주세요.");
        }
        long winnerOptionSeq = request.getWinnerOptionSeq();
        long login = requireLoginMemberSeq();
        EventBattleDto e = get(eventBattleSeq);
        if (!"OPEN".equals(e.getStatus())) {
            throw new IllegalStateException("이미 정산된 이벤트입니다.");
        }
        if (!canSettle(e, login)) {
            throw new IllegalArgumentException("승리를 확정할 권한이 없습니다.");
        }

        EventBattleOptionDto winOpt = eventBattleMapper.selectOptionById(winnerOptionSeq);
        if (winOpt == null || winOpt.getEventBattleSeq() == null || winOpt.getEventBattleSeq() != eventBattleSeq) {
            throw new IllegalArgumentException("이벤트에 속한 주제가 아닙니다.");
        }

        List<EventBattleOptionDto> opts = eventBattleMapper.selectOptionsByEventSeq(eventBattleSeq);
        long totalPool = opts.stream().mapToLong(o -> o.getPointsTotal() != null ? o.getPointsTotal() : 0L).sum();

        List<MemberStakeRow> winStakes = eventBattleMapper.sumStakesByOption(eventBattleSeq, winnerOptionSeq);
        long winSum = winStakes.stream().mapToLong(s -> s.getStakeAmount() != null ? s.getStakeAmount() : 0L).sum();

        if (totalPool == 0) {
            String modId = loginIdOrSystem();
            String modIp = RequestContext.getClientIp();
            int u = eventBattleMapper.updateSettled(eventBattleSeq, winnerOptionSeq, modId, StringUtils.hasText(modIp) ? modIp : "0.0.0.0");
            if (u == 0) {
                throw new IllegalStateException("정산 처리에 실패했습니다.");
            }
            // totalPool=0에서도 status 변경이 생기므로, SSE 스냅샷을 푸시합니다.
            sseBroadcaster.broadcastActivity(eventBattleSeq);
            return;
        }

        if (winSum <= 0) {
            refundAll(eventBattleSeq);
            String modId = loginIdOrSystem();
            String modIp = RequestContext.getClientIp();
            int u = eventBattleMapper.updateSettled(eventBattleSeq, winnerOptionSeq, modId, StringUtils.hasText(modIp) ? modIp : "0.0.0.0");
            if (u == 0) {
                throw new IllegalStateException("정산 상태 반영에 실패했습니다.");
            }
            // 환급+status 변경 후 SSE 스냅샷을 푸시합니다.
            sseBroadcaster.broadcastActivity(eventBattleSeq);
            return;
        }

        List<MemberStakeRow> sorted = new ArrayList<>(winStakes);
        sorted.sort(Comparator.comparing(MemberStakeRow::getMemberSeq));

        long[] payouts = new long[sorted.size()];
        long distributed = 0;
        for (int i = 0; i < sorted.size(); i++) {
            long stake = sorted.get(i).getStakeAmount() != null ? sorted.get(i).getStakeAmount() : 0L;
            long share = (totalPool * stake) / winSum;
            payouts[i] = share;
            distributed += share;
        }
        long remainder = totalPool - distributed;
        int rr = 0;
        while (remainder > 0 && !sorted.isEmpty()) {
            payouts[rr % payouts.length]++;
            rr++;
            remainder--;
        }

        for (int i = 0; i < sorted.size(); i++) {
            long pay = payouts[i];
            if (pay <= 0) {
                continue;
            }
            long m = sorted.get(i).getMemberSeq();
            ensureWallet(m);
            memberWalletMapper.addPoints(m, pay);
            insertLedger(
                    m,
                    WalletPointRules.REASON_EVENT_BATTLE_WIN,
                    String.format("이벤트 대결 정산 #%d 승리 (%s, %dP)", eventBattleSeq, winOpt.getLabel(), pay),
                    pay
            );
        }

        String modId = loginIdOrSystem();
        String modIp = RequestContext.getClientIp();
        int u = eventBattleMapper.updateSettled(eventBattleSeq, winnerOptionSeq, modId, StringUtils.hasText(modIp) ? modIp : "0.0.0.0");
        if (u == 0) {
            throw new IllegalStateException("정산 처리에 실패했습니다.");
        }

        // 정산 완료 후 activity 변경을 SSE로 푸시
        sseBroadcaster.broadcastActivity(eventBattleSeq);
    }

    @Override
    @Transactional
    public void closeVoteOnly(long eventBattleSeq) {
        long login = requireLoginMemberSeq();
        EventBattleDto e = get(eventBattleSeq);
        if (!"Y".equals(e.getVoteOnlyYn())) {
            throw new IllegalStateException("투표 전용 이벤트에서만 투표를 마감할 수 있습니다.");
        }
        if (!"OPEN".equals(e.getStatus())) {
            throw new IllegalStateException("이미 종료된 이벤트입니다.");
        }
        if (!canSettle(e, login)) {
            throw new IllegalArgumentException("투표 마감 권한이 없습니다.");
        }
        String modId = loginIdOrSystem();
        String modIp = RequestContext.getClientIp();
        int u = eventBattleMapper.updateSettled(
                eventBattleSeq,
                null,
                modId,
                StringUtils.hasText(modIp) ? modIp : "0.0.0.0"
        );
        if (u == 0) {
            throw new IllegalStateException("투표 마감 처리에 실패했습니다.");
        }
        sseBroadcaster.broadcastActivity(eventBattleSeq);
    }

    @Override
    @Transactional
    public void cancel(long eventBattleSeq) {
        long login = requireLoginMemberSeq();
        EventBattleDto e = get(eventBattleSeq);
        if (!"OPEN".equals(e.getStatus())) {
            throw new IllegalStateException("이미 종료된 이벤트입니다.");
        }
        if (!canSettle(e, login)) {
            throw new IllegalArgumentException("이벤트 취소 권한이 없습니다.");
        }

        String modId = loginIdOrSystem();
        String modIp = RequestContext.getClientIp();
        int u = eventBattleMapper.updateCancelled(
                eventBattleSeq,
                modId,
                StringUtils.hasText(modIp) ? modIp : "0.0.0.0"
        );
        if (u == 0) {
            throw new IllegalStateException("이벤트 취소 처리에 실패했습니다.");
        }

        // 취소 처리 시 참가자 베팅 포인트를 모두 환불하고, 이벤트는 CANCELLED로 종료
        refundAll(eventBattleSeq);

        // 취소 완료 후 activity 변경을 SSE로 푸시
        sseBroadcaster.broadcastActivity(eventBattleSeq);
    }

    private void refundAll(long eventBattleSeq) {
        List<MemberStakeRow> all = eventBattleMapper.sumStakesAllMembers(eventBattleSeq);
        for (MemberStakeRow row : all) {
            long amt = row.getStakeAmount() != null ? row.getStakeAmount() : 0L;
            if (amt <= 0) {
                continue;
            }
            long m = row.getMemberSeq();
            ensureWallet(m);
            memberWalletMapper.addPoints(m, amt);
            insertLedger(
                    m,
                    WalletPointRules.REASON_EVENT_BATTLE_REFUND,
                    String.format("이벤트 대결 환급 #%d (승리 주제 베팅 없음)", eventBattleSeq),
                    amt
            );
        }
    }

    private boolean canSettle(EventBattleDto e, long loginMemberSeq) {
        if (e.getCreatorMemberSeq() != null && e.getCreatorMemberSeq() == loginMemberSeq) {
            return true;
        }
        return securityExpressions.canUpdate(MenuAuthorities.EVENT_BATTLE);
    }

    private long requireLoginMemberSeq() {
        Long seq = RequestContext.getLoginMemberSeq();
        if (seq == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        return seq;
    }

    private void ensureWallet(long memberSeq) {
        if (memberWalletMapper.selectWalletByMemberSeq(memberSeq) == null) {
            memberWalletMapper.insertWallet(memberSeq);
        }
    }

    private String loginIdOrSystem() {
        String id = RequestContext.getLoginMemberId();
        return StringUtils.hasText(id) ? id : "SYSTEM";
    }

    private void insertLedger(long memberSeq, String reasonCode, String summary, long pointDelta) {
        MemberWalletLedgerDto row = new MemberWalletLedgerDto();
        row.setMemberSeq(memberSeq);
        row.setReasonCode(reasonCode);
        row.setSummary(summary);
        row.setPointDelta(pointDelta);
        row.setIronDelta(0);
        row.setSilverDelta(0);
        row.setGoldDelta(0);
        row.setDiamondDelta(0);
        row.setCreateId(loginIdOrSystem());
        memberWalletMapper.insertLedger(row);
    }
}
