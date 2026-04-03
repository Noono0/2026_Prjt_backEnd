package com.noonoo.prjtbackend.eventbattle.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EventBattleSaveRequest {
    private Long eventBattleSeq;
    private String title;
    /** 2개 이상 주제 라벨 */
    private List<String> optionLabels = new ArrayList<>();
    /** 1인당 투표권(기본 1) */
    private Integer voteLimitPerMember;
    /** 투표 전용 이벤트 여부(JSON: voteOnly) — 서비스에서 voteOnlyYn(Y/N)으로 저장 */
    private Boolean voteOnly;
    /** MyBatis insert용 vote_only_yn */
    private String voteOnlyYn;
    private Long creatorMemberSeq;
    private String createId;
    private String createIp;
}
