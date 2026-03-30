package com.noonoo.prjtbackend.calendarschedule.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalendarScheduleDto {
    private Long calendarScheduleSeq;
    /** GENERAL | BIRTHDAY */
    private String eventKind;
    private String title;
    /** 공통코드 A0003 code_value */
    private String categoryCode;
    /** 달력 막대 색 (#RGB / #RRGGBB) */
    private String eventColor;
    /** A0003 code_name (조회 시 조인) */
    private String categoryName;
    private String content;
    /** GENERAL: yyyy-MM-dd */
    private String startDate;
    /** GENERAL: yyyy-MM-dd inclusive */
    private String endDate;
    /** GENERAL: HH:mm (optional) */
    private String startTime;
    /** GENERAL: HH:mm (optional) */
    private String endTime;
    /** BIRTHDAY: 1–12 */
    private Integer birthMonth;
    /** BIRTHDAY: 1–31 */
    private Integer birthDay;
    private String createId;
    private String createDt;
    private String modifyDt;
}
