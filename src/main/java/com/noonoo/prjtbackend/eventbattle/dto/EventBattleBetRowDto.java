package com.noonoo.prjtbackend.eventbattle.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventBattleBetRowDto {
    private Long eventBattleBetSeq;
    private Long memberSeq;
    private String memberDisplayName;
    private Long eventBattleOptionSeq;
    private String optionLabel;
    private Long pointAmount;
    private String createDt;
}
