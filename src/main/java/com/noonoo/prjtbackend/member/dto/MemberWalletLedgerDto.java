package com.noonoo.prjtbackend.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberWalletLedgerDto {
    private Long ledgerSeq;
    private Long memberSeq;
    private String reasonCode;
    /** 표시용 요약 (한글) */
    private String summary;
    private long pointDelta;
    private int ironDelta;
    private int silverDelta;
    private int goldDelta;
    private int diamondDelta;
    private String createDt;
    private String createId;
}
