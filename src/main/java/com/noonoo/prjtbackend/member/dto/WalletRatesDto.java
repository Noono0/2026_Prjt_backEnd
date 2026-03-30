package com.noonoo.prjtbackend.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WalletRatesDto {
    private long pointsPerIron;
    private int ironPerSilver;
    private int silverPerGold;
    private int goldPerDiamond;
}
