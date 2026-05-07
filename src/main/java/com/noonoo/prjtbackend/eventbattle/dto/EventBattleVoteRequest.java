package com.noonoo.prjtbackend.eventbattle.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventBattleVoteRequest {
    private List<Long> optionSeqs = new ArrayList<>();
}
