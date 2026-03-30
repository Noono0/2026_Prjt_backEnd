package com.noonoo.prjtbackend.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PointRankingReasonBreakdownDto {
    private final String reasonCode;
    /** 사용자 표시용 한글 설명 */
    private final String reasonLabel;
    /** 해당 사유로 기간 내 획득한 포인트 합 */
    private final long points;
}
