package com.noonoo.prjtbackend.eventbattle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventBattleMyBetDto {
    private Long eventBattleOptionSeq;
    private String label;
    private Long pointAmount;
}
