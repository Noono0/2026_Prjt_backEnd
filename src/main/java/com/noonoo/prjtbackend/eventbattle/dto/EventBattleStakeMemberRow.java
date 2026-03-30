package com.noonoo.prjtbackend.eventbattle.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 승리 주제 옵션에 대한 회원별 베팅 합계 + 표시명 (정산 지급액 계산용).
 */
@Getter
@Setter
public class EventBattleStakeMemberRow {
    private Long memberSeq;
    private Long stakeAmount;
    private String memberDisplayName;
}
