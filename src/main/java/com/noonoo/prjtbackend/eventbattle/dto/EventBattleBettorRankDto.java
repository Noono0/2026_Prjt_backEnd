package com.noonoo.prjtbackend.eventbattle.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventBattleBettorRankDto {
    private Integer rank;
    private Long memberSeq;
    private String memberDisplayName;
    /** 이벤트 내 해당 회원 총 베팅 포인트 */
    private Long totalPoints;
    /** 베팅한 주제(옵션) 라벨 — 동일 이벤트에서 한 주제만 베팅 가능한 규칙 기준 */
    private String optionLabel;
}
