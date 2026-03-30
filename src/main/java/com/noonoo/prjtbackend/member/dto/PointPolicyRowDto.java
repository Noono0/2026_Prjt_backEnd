package com.noonoo.prjtbackend.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PointPolicyRowDto {
    private String policyKey;
    /** Y/N */
    private String useYn;
    /** 보상 포인트 (해당 정책에 적용되는 경우) */
    private Long rewardPoints;
    /** 임계값 (예: 추천 수) */
    private Integer thresholdInt;
    /** 상한 (예: 댓글 추가 적립 게시글당 합산) */
    private Integer capInt;
}
