package com.noonoo.prjtbackend.calendarschedule.controller;

import com.noonoo.prjtbackend.calendarschedule.dto.CalendarEventView;
import com.noonoo.prjtbackend.calendarschedule.dto.CalendarRangeRequest;
import com.noonoo.prjtbackend.calendarschedule.dto.CalendarScheduleDto;
import com.noonoo.prjtbackend.calendarschedule.dto.CalendarScheduleSaveRequest;
import com.noonoo.prjtbackend.calendarschedule.service.CalendarScheduleService;
import com.noonoo.prjtbackend.codeGroup.dto.OptionDto;
import com.noonoo.prjtbackend.common.api.ApiResponse;
import com.noonoo.prjtbackend.common.security.MenuAuthorities;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calendar-schedules")
@RequiredArgsConstructor
public class CalendarScheduleController {

    private final CalendarScheduleService calendarScheduleService;

    @GetMapping("/categories")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.CALENDAR_SCHEDULE + "')")
    public ApiResponse<List<OptionDto>> categories() {
        return ApiResponse.ok("조회 완료", calendarScheduleService.findCategoryOptions());
    }

    @PostMapping("/range")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.CALENDAR_SCHEDULE + "')")
    public ApiResponse<List<CalendarEventView>> range(@RequestBody CalendarRangeRequest request) {
        return ApiResponse.ok("일정 조회 완료", calendarScheduleService.findEventsForRange(request));
    }

    @GetMapping("/detail/{seq}")
    @PreAuthorize("@securityExpressions.canRead('" + MenuAuthorities.CALENDAR_SCHEDULE + "')")
    public ApiResponse<CalendarScheduleDto> detail(@PathVariable long seq) {
        CalendarScheduleDto dto = calendarScheduleService.detail(seq);
        if (dto == null) {
            return ApiResponse.fail("NOT_FOUND", "일정을 찾을 수 없습니다.");
        }
        return ApiResponse.ok("조회 완료", dto);
    }

    @PostMapping("/create")
    @PreAuthorize("@securityExpressions.canCreate('" + MenuAuthorities.CALENDAR_SCHEDULE + "')")
    public ApiResponse<Integer> create(@RequestBody CalendarScheduleSaveRequest request) {
        int n = calendarScheduleService.create(request);
        return ApiResponse.ok(n > 0 ? "등록되었습니다." : "등록 실패", n);
    }

    @PutMapping("/update")
    @PreAuthorize("@securityExpressions.canUpdate('" + MenuAuthorities.CALENDAR_SCHEDULE + "')")
    public ApiResponse<Integer> update(@RequestBody CalendarScheduleSaveRequest request) {
        int n = calendarScheduleService.update(request);
        return ApiResponse.ok(n > 0 ? "수정되었습니다." : "수정 실패", n);
    }

    @DeleteMapping("/delete/{seq}")
    @PreAuthorize("@securityExpressions.canDelete('" + MenuAuthorities.CALENDAR_SCHEDULE + "')")
    public ApiResponse<Integer> delete(@PathVariable long seq) {
        int n = calendarScheduleService.delete(seq);
        return ApiResponse.ok(n > 0 ? "삭제되었습니다." : "삭제 실패", n);
    }
}
