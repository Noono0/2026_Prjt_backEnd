package com.noonoo.prjtbackend.calendarschedule.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * FullCalendar 표시용 (종일 이벤트, end는 배타일).
 */
@Getter
@Builder
public class CalendarEventView {
    /** FC event id (생일 연도별은 seq-yyyy-MM-dd 형태) */
    private String id;
    private long calendarScheduleSeq;
    /** DB 저장 제목(카테고리 접두 없음). 화면 표시는 categoryName과 조합 */
    private String title;
    /** 공통코드 A0003 코드명(없으면 null) */
    private String categoryName;
    /** yyyy-MM-dd */
    private String start;
    /** yyyy-MM-dd (FullCalendar 종일 배타) */
    private String end;
    private boolean allDay;
    /** GENERAL | BIRTHDAY */
    private String eventKind;
    private String backgroundColor;
    /** 등록자 회원 ID (달력에서 수정/상세 분기용) */
    private String createId;
}
