package com.noonoo.prjtbackend.member.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PointRankingEntryDto {
    private final int rank;
    private final long memberSeq;
    private final String memberId;
    private final String displayLabel;
    private final long pointsEarned;
    /** 기간 내 획득(양수) 사유별 합계, 포인트 많은 순 */
    private final List<PointRankingReasonBreakdownDto> breakdown;
}
