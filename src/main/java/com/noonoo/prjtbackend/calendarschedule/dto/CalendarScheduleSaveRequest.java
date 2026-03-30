package com.noonoo.prjtbackend.calendarschedule.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalendarScheduleSaveRequest {
    private Long calendarScheduleSeq;
    /** GENERAL | BIRTHDAY */
    private String eventKind;
    private String title;
    /** 공통코드 A0003 code_value */
    private String categoryCode;
    /** 달력 표시 색 (#RGB / #RRGGBB), 비우면 등록 시 랜덤·수정 시 기존값 유지 */
    private String eventColor;
    private String content;
    private String startDate;
    private String endDate;
    /** GENERAL: HH:mm (optional) */
    private String startTime;
    /** GENERAL: HH:mm (optional) */
    private String endTime;
    private Integer birthMonth;
    private Integer birthDay;

    /** insert/update 시 서비스에서 설정 */
    private String createId;
    private String createIp;
    private String modifyId;
    private String modifyIp;
}
