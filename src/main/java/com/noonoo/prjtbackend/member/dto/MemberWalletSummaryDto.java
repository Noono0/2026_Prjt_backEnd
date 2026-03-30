package com.noonoo.prjtbackend.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberWalletSummaryDto {
    private long memberSeq;
    private long pointBalance;
    private int ironQty;
    private int silverQty;
    private int goldQty;
    private int diamondQty;
    private WalletRatesDto rates;
}
