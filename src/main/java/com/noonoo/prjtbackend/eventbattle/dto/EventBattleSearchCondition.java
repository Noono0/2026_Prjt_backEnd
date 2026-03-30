package com.noonoo.prjtbackend.eventbattle.dto;

import com.noonoo.prjtbackend.common.paging.PageRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventBattleSearchCondition extends PageRequest {

    /** OPEN | SETTLED | CANCELLED | null(전체) */
    private String status;
}
