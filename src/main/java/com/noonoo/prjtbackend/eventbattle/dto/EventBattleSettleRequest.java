package com.noonoo.prjtbackend.eventbattle.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventBattleSettleRequest {
    /** 승리한 주제 event_battle_option_seq */
    private Long winnerOptionSeq;
}
