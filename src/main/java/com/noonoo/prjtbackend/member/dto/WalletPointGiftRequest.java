package com.noonoo.prjtbackend.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WalletPointGiftRequest {

    /** 선물 받을 회원 */
    private Long toMemberSeq;

    /** 선물 포인트 (1 이상) */
    private Long points;
}
