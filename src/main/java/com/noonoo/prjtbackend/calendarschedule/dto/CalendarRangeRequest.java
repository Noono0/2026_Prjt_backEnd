package com.noonoo.prjtbackend.calendarschedule.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalendarRangeRequest {
    /** yyyy-MM-dd */
    private String from;
    /** yyyy-MM-dd */
    private String to;
}
