package com.noonoo.prjtbackend.member.dto;

import lombok.Getter;
import lombok.Setter;

/** 포인트 랭킹용: 회원·사유별 기간 내 획득 합(양수만) */
@Getter
@Setter
public class PointRankingBreakdownRowDto {
    private long memberSeq;
    private String reasonCode;
    private long pointsEarned;
}
