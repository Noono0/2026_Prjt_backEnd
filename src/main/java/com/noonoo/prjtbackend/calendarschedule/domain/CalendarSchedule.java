package com.noonoo.prjtbackend.calendarschedule.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@ToString
@Table(name = "calendar_schedule")
public class CalendarSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "calendar_schedule_seq")
    private Long calendarScheduleSeq;

    @Column(name = "event_kind", length = 20, nullable = false)
    private String eventKind;

    @Column(name = "title", length = 500, nullable = false)
    private String title;

    @Column(name = "category_code", length = 100)
    private String categoryCode;

    @Column(name = "event_color", length = 20)
    private String eventColor;

    @Column(name = "content", columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "birth_month")
    private Integer birthMonth;

    @Column(name = "birth_day")
    private Integer birthDay;

    @Column(name = "use_yn", length = 1, nullable = false)
    private String useYn;

    @Column(name = "create_id", length = 50)
    private String createId;

    @Column(name = "create_ip", length = 45)
    private String createIp;

    @Column(name = "create_dt")
    private LocalDateTime createDt;

    @Column(name = "modify_id", length = 50)
    private String modifyId;

    @Column(name = "modify_ip", length = 45)
    private String modifyIp;

    @Column(name = "modify_dt")
    private LocalDateTime modifyDt;
}
