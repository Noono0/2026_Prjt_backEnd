package com.noonoo.prjtbackend.eventbattle.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventBattleBetRequest {
    /** event_battle_option.event_battle_option_seq */
    private Long eventBattleOptionSeq;
    private Long points;
}
