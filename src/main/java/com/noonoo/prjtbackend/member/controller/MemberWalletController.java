package com.noonoo.prjtbackend.member.controller;

import com.noonoo.prjtbackend.common.api.ApiResponse;
import com.noonoo.prjtbackend.common.paging.PageResponse;
import com.noonoo.prjtbackend.member.dto.MemberWalletLedgerDto;
import com.noonoo.prjtbackend.member.dto.MemberWalletSummaryDto;
import com.noonoo.prjtbackend.member.dto.WalletLedgerSearchCondition;
import com.noonoo.prjtbackend.member.dto.WalletPointGiftRequest;
import com.noonoo.prjtbackend.member.dto.WalletQuantityRequest;
import com.noonoo.prjtbackend.member.service.MemberWalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members/me/wallet")
@RequiredArgsConstructor
public class MemberWalletController {

    private final MemberWalletService memberWalletService;

    @GetMapping
    @PreAuthorize("@securityExpressions.isAuthenticatedOrPermitAll()")
    public ApiResponse<MemberWalletSummaryDto> wallet() {
        return ApiResponse.ok("조회 완료", memberWalletService.getMyWallet());
    }

    @GetMapping("/ledger")
    @PreAuthorize("@securityExpressions.isAuthenticatedOrPermitAll()")
    public ApiResponse<PageResponse<MemberWalletLedgerDto>> ledger(WalletLedgerSearchCondition condition) {
        return ApiResponse.ok("조회 완료", memberWalletService.getMyLedger(condition));
    }

    @PostMapping("/purchase-iron")
    @PreAuthorize("@securityExpressions.isAuthenticatedOrPermitAll()")
    public ApiResponse<Void> purchaseIron(@RequestBody WalletQuantityRequest request) {
        memberWalletService.purchaseIron(request);
        return ApiResponse.ok("아이언 티켓을 구매했습니다.", null);
    }

    @PostMapping("/exchange/iron-silver")
    @PreAuthorize("@securityExpressions.isAuthenticatedOrPermitAll()")
    public ApiResponse<Void> exchangeIronSilver(@RequestBody WalletQuantityRequest request) {
        memberWalletService.exchangeIronToSilver(request);
        return ApiResponse.ok("실버 티켓으로 교환했습니다.", null);
    }

    @PostMapping("/exchange/silver-gold")
    @PreAuthorize("@securityExpressions.isAuthenticatedOrPermitAll()")
    public ApiResponse<Void> exchangeSilverGold(@RequestBody WalletQuantityRequest request) {
        memberWalletService.exchangeSilverToGold(request);
        return ApiResponse.ok("골드 티켓으로 교환했습니다.", null);
    }

    @PostMapping("/exchange/gold-diamond")
    @PreAuthorize("@securityExpressions.isAuthenticatedOrPermitAll()")
    public ApiResponse<Void> exchangeGoldDiamond(@RequestBody WalletQuantityRequest request) {
        memberWalletService.exchangeGoldToDiamond(request);
        return ApiResponse.ok("다이아 티켓으로 교환했습니다.", null);
    }

    @PostMapping("/gift-points")
    @PreAuthorize("@securityExpressions.isAuthenticatedOrPermitAll()")
    public ApiResponse<Void> giftPoints(@RequestBody WalletPointGiftRequest request) {
        memberWalletService.giftPoints(request);
        return ApiResponse.ok("포인트를 선물했습니다.", null);
    }
}
