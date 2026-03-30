package com.noonoo.prjtbackend.eventbattle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 종료 후 결과 모달용 — 승리 주제 쪽 정산 지급 상위 행.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventBattleWinnerPayoutRowDto {
    /** 승리측 지급액 기준 순위 (1부터) */
    private Integer rank;
    private String memberDisplayName;
    /** 해당 주제에 건 베팅 합계 */
    private Long stakePoints;
    /** 이벤트 풀 비례 정산 지급 포인트 */
    private Long payoutPoints;
}
