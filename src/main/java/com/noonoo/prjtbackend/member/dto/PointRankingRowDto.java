package com.noonoo.prjtbackend.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PointRankingRowDto {
    private long memberSeq;
    private String memberId;
    /** 닉네임(없으면 아이디) — 회원명(member_name) 미사용 */
    private String displayLabel;
    /** 기간 내 획득 포인트 합 (양수만 집계) */
    private long pointsEarned;
}
