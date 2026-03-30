package com.noonoo.prjtbackend.calendarschedule.mapper;

import com.noonoo.prjtbackend.calendarschedule.dto.CalendarScheduleDto;
import com.noonoo.prjtbackend.calendarschedule.dto.CalendarScheduleSaveRequest;
import com.noonoo.prjtbackend.codeGroup.dto.OptionDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CalendarScheduleMapper {

    List<OptionDto> findScheduleCategoryOptions();

    List<CalendarScheduleDto> selectGeneralInRange(@Param("from") String from, @Param("to") String to);

    List<CalendarScheduleDto> selectAllBirthdays();

    CalendarScheduleDto selectById(@Param("calendarScheduleSeq") long calendarScheduleSeq);

    int insertSchedule(CalendarScheduleSaveRequest request);

    int updateSchedule(CalendarScheduleSaveRequest request);

    int softDelete(@Param("calendarScheduleSeq") long calendarScheduleSeq);
}
