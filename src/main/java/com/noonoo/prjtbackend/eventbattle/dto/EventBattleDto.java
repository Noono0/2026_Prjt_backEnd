package com.noonoo.prjtbackend.eventbattle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventBattleDto {
    private Long eventBattleSeq;
    private String title;
    private String status;
    /** 1인당 투표권 */
    private Integer voteLimitPerMember;
    /** Y: 투표 전용(베팅 불가), N: 베팅 이벤트(투표 API 불가) */
    private String voteOnlyYn;
    /** 정산 후 승리 주제 옵션 PK */
    private Long winnerOptionSeq;
    private String winnerLabel;
    private Long creatorMemberSeq;
    private String creatorMemberId;
    private String creatorDisplayName;
    private String creatorProfileImageUrl;
    private String createDt;
    private String settleDt;
    /** 목록용: 주제 라벨 요약 */
    private String optionLabelsPreview;
    /** 상세: 주제별 집계 (2~5) */
    @Builder.Default
    private List<EventBattleOptionDto> options = new ArrayList<>();
}
