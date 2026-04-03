package com.noonoo.prjtbackend.member.serviceImpl;

import com.noonoo.prjtbackend.common.config.RequestContext;
import com.noonoo.prjtbackend.common.paging.PageResponse;
import com.noonoo.prjtbackend.common.paging.PagingUtils;
import com.noonoo.prjtbackend.member.MemberDisplayNames;
import com.noonoo.prjtbackend.member.dto.MemberDto;
import com.noonoo.prjtbackend.member.dto.MemberWalletBalance;
import com.noonoo.prjtbackend.member.dto.MemberWalletLedgerDto;
import com.noonoo.prjtbackend.member.dto.MemberWalletSummaryDto;
import com.noonoo.prjtbackend.member.dto.WalletLedgerSearchCondition;
import com.noonoo.prjtbackend.member.dto.WalletPointGiftRequest;
import com.noonoo.prjtbackend.member.dto.WalletQuantityRequest;
import com.noonoo.prjtbackend.member.dto.WalletRatesDto;
import com.noonoo.prjtbackend.member.mapper.MemberMapper;
import com.noonoo.prjtbackend.member.mapper.MemberWalletMapper;
import com.noonoo.prjtbackend.member.service.MemberWalletService;
import com.noonoo.prjtbackend.member.wallet.WalletConstants;
import com.noonoo.prjtbackend.member.wallet.WalletPointRules;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberWalletServiceImpl implements MemberWalletService {

    private final MemberWalletMapper memberWalletMapper;
    private final MemberMapper memberMapper;

    private static WalletRatesDto rates() {
        return WalletRatesDto.builder()
                .pointsPerIron(WalletConstants.POINTS_PER_IRON)
                .ironPerSilver(WalletConstants.IRON_PER_SILVER)
                .silverPerGold(WalletConstants.SILVER_PER_GOLD)
                .goldPerDiamond(WalletConstants.GOLD_PER_DIAMOND)
                .build();
    }

    private long requireMemberSeq() {
        Long seq = RequestContext.getLoginMemberSeq();
        if (seq == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        return seq;
    }

    private void ensureWallet(long memberSeq) {
        MemberWalletBalance w = memberWalletMapper.selectWalletByMemberSeq(memberSeq);
        if (w == null) {
            memberWalletMapper.insertWallet(memberSeq);
        }
    }

    private String loginIdOrSystem() {
        String id = RequestContext.getLoginMemberId();
        return StringUtils.hasText(id) ? id : "SYSTEM";
    }

    private void insertLedger(
            long memberSeq,
            String reasonCode,
            String summary,
            long pointDelta,
            int ironDelta,
            int silverDelta,
            int goldDelta,
            int diamondDelta
    ) {
        MemberWalletLedgerDto row = new MemberWalletLedgerDto();
        row.setMemberSeq(memberSeq);
        row.setReasonCode(reasonCode);
        row.setSummary(summary);
        row.setPointDelta(pointDelta);
        row.setIronDelta(ironDelta);
        row.setSilverDelta(silverDelta);
        row.setGoldDelta(goldDelta);
        row.setDiamondDelta(diamondDelta);
        row.setCreateId(loginIdOrSystem());
        memberWalletMapper.insertLedger(row);
    }

    @Override
    public MemberWalletSummaryDto getMyWallet() {
        long memberSeq = requireMemberSeq();
        ensureWallet(memberSeq);
        MemberWalletBalance b = memberWalletMapper.selectWalletByMemberSeq(memberSeq);
        if (b == null) {
            throw new IllegalStateException("지갑 정보를 불러올 수 없습니다.");
        }
        return MemberWalletSummaryDto.builder()
                .memberSeq(memberSeq)
                .pointBalance(b.getPointBalance() != null ? b.getPointBalance() : 0L)
                .ironQty(b.getIronQty() != null ? b.getIronQty() : 0)
                .silverQty(b.getSilverQty() != null ? b.getSilverQty() : 0)
                .goldQty(b.getGoldQty() != null ? b.getGoldQty() : 0)
                .diamondQty(b.getDiamondQty() != null ? b.getDiamondQty() : 0)
                .rates(rates())
                .build();
    }

    @Override
    public PageResponse<MemberWalletLedgerDto> getMyLedger(WalletLedgerSearchCondition condition) {
        long memberSeq = requireMemberSeq();
        if (condition == null) {
            condition = new WalletLedgerSearchCondition();
        }
        condition.setMemberSeq(memberSeq);
        long total = memberWalletMapper.countLedger(memberSeq);
        List<MemberWalletLedgerDto> items = memberWalletMapper.selectLedgerPage(condition);
        return PagingUtils.toPageResponse(condition, total, items);
    }

    @Override
    @Transactional
    public void purchaseIron(WalletQuantityRequest request) {
        long memberSeq = requireMemberSeq();
        int qty = request != null && request.getQuantity() != null ? request.getQuantity() : 0;
        if (qty < 1) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        }
        ensureWallet(memberSeq);
        long cost = WalletConstants.POINTS_PER_IRON * (long) qty;
        int n = memberWalletMapper.purchaseIron(memberSeq, cost, qty);
        if (n == 0) {
            throw new IllegalArgumentException("포인트가 부족합니다. (필요: " + cost + "P)");
        }
        insertLedger(
                memberSeq,
                "PURCHASE_IRON",
                String.format("아이언 티켓 구매 (%d장, %dP 차감)", qty, cost),
                -cost,
                qty,
                0,
                0,
                0
        );
    }

    @Override
    @Transactional
    public void exchangeIronToSilver(WalletQuantityRequest request) {
        long memberSeq = requireMemberSeq();
        int times = request != null && request.getQuantity() != null ? request.getQuantity() : 0;
        if (times < 1) {
            throw new IllegalArgumentException("횟수는 1 이상이어야 합니다.");
        }
        ensureWallet(memberSeq);
        int ironCost = WalletConstants.IRON_PER_SILVER * times;
        MemberWalletBalance bal = memberWalletMapper.selectWalletByMemberSeq(memberSeq);
        int ironQty = bal != null && bal.getIronQty() != null ? bal.getIronQty() : 0;
        if (ironQty < ironCost) {
            throw new IllegalArgumentException(
                    "아이언 티켓이 부족합니다. (보유: " + ironQty + "장, 필요: " + ironCost + "장 → 실버 " + times + "장)");
        }
        int n = memberWalletMapper.exchangeIronToSilver(memberSeq, ironCost, times);
        if (n == 0) {
            throw new IllegalArgumentException(
                    "아이언 티켓이 부족합니다. (필요: " + ironCost + "장 → 실버 " + times + "장)");
        }
        insertLedger(
                memberSeq,
                "EXCHANGE_IRON_SILVER",
                String.format("아이언 → 실버 교환 (아이언 %d장 차감, 실버 %d장 획득)", ironCost, times),
                0,
                -ironCost,
                times,
                0,
                0
        );
    }

    @Override
    @Transactional
    public void exchangeSilverToGold(WalletQuantityRequest request) {
        long memberSeq = requireMemberSeq();
        int times = request != null && request.getQuantity() != null ? request.getQuantity() : 0;
        if (times < 1) {
            throw new IllegalArgumentException("횟수는 1 이상이어야 합니다.");
        }
        ensureWallet(memberSeq);
        int silverCost = WalletConstants.SILVER_PER_GOLD * times;
        MemberWalletBalance bal = memberWalletMapper.selectWalletByMemberSeq(memberSeq);
        int silverQty = bal != null && bal.getSilverQty() != null ? bal.getSilverQty() : 0;
        if (silverQty < silverCost) {
            throw new IllegalArgumentException(
                    "실버 티켓이 부족합니다. (보유: " + silverQty + "장, 필요: " + silverCost + "장 → 골드 " + times + "장)");
        }
        int n = memberWalletMapper.exchangeSilverToGold(memberSeq, silverCost, times);
        if (n == 0) {
            throw new IllegalArgumentException(
                    "실버 티켓이 부족합니다. (필요: " + silverCost + "장 → 골드 " + times + "장)");
        }
        insertLedger(
                memberSeq,
                "EXCHANGE_SILVER_GOLD",
                String.format("실버 → 골드 교환 (실버 %d장 차감, 골드 %d장 획득)", silverCost, times),
                0,
                0,
                -silverCost,
                times,
                0
        );
    }

    @Override
    @Transactional
    public void exchangeGoldToDiamond(WalletQuantityRequest request) {
        long memberSeq = requireMemberSeq();
        int times = request != null && request.getQuantity() != null ? request.getQuantity() : 0;
        if (times < 1) {
            throw new IllegalArgumentException("횟수는 1 이상이어야 합니다.");
        }
        ensureWallet(memberSeq);
        int goldCost = WalletConstants.GOLD_PER_DIAMOND * times;
        MemberWalletBalance bal = memberWalletMapper.selectWalletByMemberSeq(memberSeq);
        int goldQty = bal != null && bal.getGoldQty() != null ? bal.getGoldQty() : 0;
        if (goldQty < goldCost) {
            throw new IllegalArgumentException(
                    "골드 티켓이 부족합니다. (보유: " + goldQty + "장, 필요: " + goldCost + "장 → 다이아 " + times + "장)");
        }
        int n = memberWalletMapper.exchangeGoldToDiamond(memberSeq, goldCost, times);
        if (n == 0) {
            throw new IllegalArgumentException(
                    "골드 티켓이 부족합니다. (필요: " + goldCost + "장 → 다이아 " + times + "장)");
        }
        insertLedger(
                memberSeq,
                "EXCHANGE_GOLD_DIAMOND",
                String.format("골드 → 다이아 교환 (골드 %d장 차감, 다이아 %d장 획득)", goldCost, times),
                0,
                0,
                0,
                -goldCost,
                times
        );
    }

    @Override
    @Transactional
    public void giftPoints(WalletPointGiftRequest request) {
        long fromSeq = requireMemberSeq();
        if (request == null || request.getToMemberSeq() == null || request.getToMemberSeq() <= 0) {
            throw new IllegalArgumentException("받는 회원을 지정해 주세요.");
        }
        long toSeq = request.getToMemberSeq();
        if (fromSeq == toSeq) {
            throw new IllegalArgumentException("본인에게는 선물할 수 없습니다.");
        }
        long pts = request.getPoints() != null ? request.getPoints() : 0L;
        if (pts < 1) {
            throw new IllegalArgumentException("선물 포인트는 1 이상이어야 합니다.");
        }
        MemberDto toMember = memberMapper.findMemberById(toSeq);
        if (toMember == null) {
            throw new IllegalArgumentException("받는 회원을 찾을 수 없습니다.");
        }
        MemberDto fromMember = memberMapper.findMemberById(fromSeq);
        if (fromMember == null) {
            throw new IllegalArgumentException("회원 정보를 확인할 수 없습니다.");
        }
        ensureWallet(fromSeq);
        ensureWallet(toSeq);
        int n = memberWalletMapper.subtractPointsIfEnough(fromSeq, pts);
        if (n == 0) {
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }
        memberWalletMapper.addPoints(toSeq, pts);
        String toDisplay = MemberDisplayNames.fromMember(toMember);
        String fromDisplay = MemberDisplayNames.fromMember(fromMember);
        String toLabel =
                StringUtils.hasText(toDisplay)
                        ? toDisplay
                        : (StringUtils.hasText(toMember.getMemberId()) ? toMember.getMemberId() : ("회원" + toSeq));
        String fromLabel =
                StringUtils.hasText(fromDisplay)
                        ? fromDisplay
                        : (StringUtils.hasText(fromMember.getMemberId())
                                ? fromMember.getMemberId()
                                : ("회원" + fromSeq));
        insertLedger(
                fromSeq,
                WalletPointRules.REASON_POINT_GIFT_SENT,
                String.format("포인트 선물 보냄 → %s (%dP)", toLabel, pts),
                -pts,
                0,
                0,
                0,
                0
        );
        insertLedger(
                toSeq,
                WalletPointRules.REASON_POINT_GIFT_RECEIVED,
                String.format("포인트 선물 받음 ← %s (%dP)", fromLabel, pts),
                pts,
                0,
                0,
                0,
                0
        );
    }
}
