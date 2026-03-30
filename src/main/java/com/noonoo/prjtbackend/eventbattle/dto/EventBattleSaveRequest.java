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
    /** 2~5개 주제 라벨 */
    private List<String> optionLabels = new ArrayList<>();
    private Long creatorMemberSeq;
    private String createId;
    private String createIp;
}
