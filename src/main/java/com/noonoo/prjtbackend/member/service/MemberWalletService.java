package com.noonoo.prjtbackend.member.service;

import com.noonoo.prjtbackend.common.paging.PageResponse;
import com.noonoo.prjtbackend.member.dto.MemberWalletLedgerDto;
import com.noonoo.prjtbackend.member.dto.MemberWalletSummaryDto;
import com.noonoo.prjtbackend.member.dto.WalletLedgerSearchCondition;
import com.noonoo.prjtbackend.member.dto.WalletPointGiftRequest;
import com.noonoo.prjtbackend.member.dto.WalletQuantityRequest;

public interface MemberWalletService {

    MemberWalletSummaryDto getMyWallet();

    PageResponse<MemberWalletLedgerDto> getMyLedger(WalletLedgerSearchCondition condition);

    void purchaseIron(WalletQuantityRequest request);

    void exchangeIronToSilver(WalletQuantityRequest request);

    void exchangeSilverToGold(WalletQuantityRequest request);

    void exchangeGoldToDiamond(WalletQuantityRequest request);

    /** 로그인 회원 → toMemberSeq 로 포인트 선물 (차감·증가·원장을 한 트랜잭션) */
    void giftPoints(WalletPointGiftRequest request);
}
