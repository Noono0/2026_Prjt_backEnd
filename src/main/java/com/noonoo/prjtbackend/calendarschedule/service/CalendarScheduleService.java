package com.noonoo.prjtbackend.calendarschedule.service;

import com.noonoo.prjtbackend.calendarschedule.dto.CalendarEventView;
import com.noonoo.prjtbackend.calendarschedule.dto.CalendarRangeRequest;
import com.noonoo.prjtbackend.calendarschedule.dto.CalendarScheduleDto;
import com.noonoo.prjtbackend.calendarschedule.dto.CalendarScheduleSaveRequest;
import com.noonoo.prjtbackend.codeGroup.dto.OptionDto;

import java.util.List;

public interface CalendarScheduleService {

    List<OptionDto> findCategoryOptions();

    List<CalendarEventView> findEventsForRange(CalendarRangeRequest request);

    CalendarScheduleDto detail(long calendarScheduleSeq);

    int create(CalendarScheduleSaveRequest request);

    int update(CalendarScheduleSaveRequest request);

    int delete(long calendarScheduleSeq);
}
