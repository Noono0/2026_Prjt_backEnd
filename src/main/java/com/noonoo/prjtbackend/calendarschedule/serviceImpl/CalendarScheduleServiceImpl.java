package com.noonoo.prjtbackend.calendarschedule.serviceImpl;

import com.noonoo.prjtbackend.codeGroup.dto.OptionDto;
import com.noonoo.prjtbackend.calendarschedule.dto.CalendarEventView;
import com.noonoo.prjtbackend.calendarschedule.dto.CalendarRangeRequest;
import com.noonoo.prjtbackend.calendarschedule.dto.CalendarScheduleDto;
import com.noonoo.prjtbackend.calendarschedule.dto.CalendarScheduleSaveRequest;
import com.noonoo.prjtbackend.calendarschedule.mapper.CalendarScheduleMapper;
import com.noonoo.prjtbackend.calendarschedule.service.CalendarScheduleService;
import com.noonoo.prjtbackend.common.config.RequestContext;
import com.noonoo.prjtbackend.contentfilter.service.ContentFilterApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CalendarScheduleServiceImpl implements CalendarScheduleService {

    private static final Set<String> KINDS = Set.of("GENERAL", "BIRTHDAY");
    private static final String COLOR_GENERAL = "#0ea5e9";
    private static final String COLOR_BIRTHDAY = "#db2777";
    private static final Pattern HEX_COLOR = Pattern.compile("^#([0-9a-fA-F]{3}|[0-9a-fA-F]{6})$");

    /** 프론트 팔레트와 동일 — 미지정·오류 시 기본색 폴백 */
    private static final String[] EVENT_COLOR_PALETTE = {
        "#0ea5e9", "#8b5cf6", "#22c55e", "#f59e0b", "#ef4444",
        "#ec4899", "#14b8a6", "#6366f1", "#84cc16", "#d946ef"
    };

    private final CalendarScheduleMapper calendarScheduleMapper;
    private final ContentFilterApplyService contentFilterApplyService;

    @Override
    public List<OptionDto> findCategoryOptions() {
        return calendarScheduleMapper.findScheduleCategoryOptions();
    }

    @Override
    public List<CalendarEventView> findEventsForRange(CalendarRangeRequest request) {
        if (request == null || !StringUtils.hasText(request.getFrom()) || !StringUtils.hasText(request.getTo())) {
            throw new IllegalArgumentException("조회 시작일·종료일이 필요합니다.");
        }
        LocalDate from = LocalDate.parse(request.getFrom().trim());
        LocalDate to = LocalDate.parse(request.getTo().trim());
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("시작일이 종료일보다 늦을 수 없습니다.");
        }
        String fromStr = from.toString();
        String toStr = to.toString();

        List<CalendarEventView> out = new ArrayList<>();
        for (CalendarScheduleDto row : calendarScheduleMapper.selectGeneralInRange(fromStr, toStr)) {
            out.add(toGeneralEvent(row));
        }
        for (CalendarScheduleDto row : calendarScheduleMapper.selectAllBirthdays()) {
            out.addAll(expandBirthday(row, from, to));
        }
        return out;
    }

    private CalendarEventView toGeneralEvent(CalendarScheduleDto row) {
        LocalDate s = LocalDate.parse(row.getStartDate());
        LocalDate e = LocalDate.parse(row.getEndDate());
        LocalDateTime[] bounds = resolveGeneralBounds(s, e, row.getStartTime(), row.getEndTime());
        if (bounds == null) {
            LocalDate endExclusive = e.plusDays(1);
            return CalendarEventView.builder()
                    .id(String.valueOf(row.getCalendarScheduleSeq()))
                    .calendarScheduleSeq(row.getCalendarScheduleSeq())
                    .title(row.getTitle() != null ? row.getTitle().trim() : "")
                    .categoryName(row.getCategoryName())
                    .start(s.toString())
                    .end(endExclusive.toString())
                    .allDay(true)
                    .eventKind("GENERAL")
                    .backgroundColor(resolveDisplayColor(row.getEventColor(), COLOR_GENERAL))
                    .createId(row.getCreateId())
                    .build();
        }
        LocalDateTime startDT = bounds[0];
        LocalDateTime endDT = bounds[1];
        if (!endDT.isAfter(startDT)) {
            endDT = startDT.plusMinutes(1);
        }
        return CalendarEventView.builder()
                .id(String.valueOf(row.getCalendarScheduleSeq()))
                .calendarScheduleSeq(row.getCalendarScheduleSeq())
                .title(row.getTitle() != null ? row.getTitle().trim() : "")
                .categoryName(row.getCategoryName())
                .start(startDT.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .end(endDT.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .allDay(false)
                .eventKind("GENERAL")
                .backgroundColor(resolveDisplayColor(row.getEventColor(), COLOR_GENERAL))
                .createId(row.getCreateId())
                .build();
    }

    private static String resolveDisplayColor(String stored, String fallback) {
        if (StringUtils.hasText(stored)) {
            String s = stored.trim();
            if (HEX_COLOR.matcher(s).matches()) {
                return s;
            }
        }
        return fallback;
    }

    private static String randomPaletteColor() {
        return EVENT_COLOR_PALETTE[ThreadLocalRandom.current().nextInt(EVENT_COLOR_PALETTE.length)];
    }

    /**
     * 시간이 하나도 없으면 null(종일). 있으면 [시작, 종료] 로컬 시각(종료는 FC 배타 규칙에 맞게 그대로 사용).
     */
    private LocalDateTime[] resolveGeneralBounds(LocalDate s, LocalDate e, String startTimeStr, String endTimeStr) {
        LocalTime st = parseToLocalTime(startTimeStr);
        LocalTime et = parseToLocalTime(endTimeStr);
        boolean hasAny = st != null || et != null;
        if (!hasAny) {
            return null;
        }
        if (s.equals(e)) {
            if (st == null) {
                st = LocalTime.MIN;
            }
            if (et == null) {
                et = st.plusHours(1);
            }
            if (!et.isAfter(st)) {
                throw new IllegalArgumentException("같은 날 일정은 종료 시각이 시작 시각보다 이후여야 합니다.");
            }
        } else {
            if (st == null) {
                st = LocalTime.MIN;
            }
            if (et == null) {
                et = LocalTime.of(23, 59, 59);
            }
            if (!LocalDateTime.of(e, et).isAfter(LocalDateTime.of(s, st))) {
                throw new IllegalArgumentException("종료 시각이 시작 시각보다 이후여야 합니다.");
            }
        }
        return new LocalDateTime[] { LocalDateTime.of(s, st), LocalDateTime.of(e, et) };
    }

    private LocalTime parseToLocalTime(String raw) {
        if (!StringUtils.hasText(raw)) {
            return null;
        }
        String t = raw.trim();
        if (t.isEmpty()) {
            return null;
        }
        if (t.length() == 5) {
            return LocalTime.parse(t + ":00");
        }
        return LocalTime.parse(t);
    }

    private String normalizeTimeForDb(String raw) {
        LocalTime t = parseToLocalTime(raw);
        return t == null ? null : t.toString();
    }

    private boolean hasAnyTime(CalendarScheduleSaveRequest request) {
        return StringUtils.hasText(request.getStartTime()) || StringUtils.hasText(request.getEndTime());
    }

    private List<CalendarEventView> expandBirthday(CalendarScheduleDto row, LocalDate from, LocalDate to) {
        List<CalendarEventView> list = new ArrayList<>();
        Integer bm = row.getBirthMonth();
        Integer bd = row.getBirthDay();
        if (bm == null || bd == null) {
            return list;
        }
        int m = bm;
        int d = bd;
        String baseTitle = StringUtils.hasText(row.getTitle()) ? row.getTitle().trim() : "생일";
        for (int y = from.getYear(); y <= to.getYear(); y++) {
            LocalDate occ;
            try {
                occ = LocalDate.of(y, m, d);
            } catch (DateTimeException ex) {
                int last = YearMonth.of(y, m).lengthOfMonth();
                occ = LocalDate.of(y, m, Math.min(d, last));
            }
            if (occ.isBefore(from) || occ.isAfter(to)) {
                continue;
            }
            String dayStr = occ.toString();
            list.add(CalendarEventView.builder()
                    .id(row.getCalendarScheduleSeq() + "-" + dayStr)
                    .calendarScheduleSeq(row.getCalendarScheduleSeq())
                    .title(baseTitle + " 생일")
                    .categoryName(row.getCategoryName())
                    .start(dayStr)
                    .end(occ.plusDays(1).toString())
                    .allDay(true)
                    .eventKind("BIRTHDAY")
                    .backgroundColor(resolveDisplayColor(row.getEventColor(), COLOR_BIRTHDAY))
                    .createId(row.getCreateId())
                    .build());
        }
        return list;
    }

    @Override
    public CalendarScheduleDto detail(long calendarScheduleSeq) {
        return calendarScheduleMapper.selectById(calendarScheduleSeq);
    }

    @Override
    @Transactional
    public int create(CalendarScheduleSaveRequest request) {
        validateAndNormalize(request);
        if (!StringUtils.hasText(request.getEventColor())) {
            request.setEventColor(randomPaletteColor());
        }
        applyAuditForWrite(request, true);
        request.setTitle(contentFilterApplyService.applyField("제목", request.getTitle()));
        request.setContent(contentFilterApplyService.applyField("내용", request.getContent()));
        return calendarScheduleMapper.insertSchedule(request);
    }

    @Override
    @Transactional
    public int update(CalendarScheduleSaveRequest request) {
        if (request.getCalendarScheduleSeq() == null) {
            throw new IllegalArgumentException("일련번호가 필요합니다.");
        }
        validateAndNormalize(request);
        if (!StringUtils.hasText(request.getEventColor())) {
            CalendarScheduleDto prev = calendarScheduleMapper.selectById(request.getCalendarScheduleSeq());
            if (prev != null && StringUtils.hasText(prev.getEventColor())) {
                request.setEventColor(prev.getEventColor().trim());
            } else {
                request.setEventColor(randomPaletteColor());
            }
        }
        applyAuditForWrite(request, false);
        request.setTitle(contentFilterApplyService.applyField("제목", request.getTitle()));
        request.setContent(contentFilterApplyService.applyField("내용", request.getContent()));
        return calendarScheduleMapper.updateSchedule(request);
    }

    @Override
    @Transactional
    public int delete(long calendarScheduleSeq) {
        return calendarScheduleMapper.softDelete(calendarScheduleSeq);
    }

    private void applyAuditForWrite(CalendarScheduleSaveRequest request, boolean isCreate) {
        String login = RequestContext.getLoginMemberId();
        String id = StringUtils.hasText(login) ? login : "SYSTEM";
        String ip = RequestContext.getClientIp();
        if (isCreate) {
            request.setCreateId(id);
            request.setCreateIp(ip);
        }
        request.setModifyId(id);
        request.setModifyIp(ip);
    }

    private void validateAndNormalize(CalendarScheduleSaveRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("요청이 없습니다.");
        }
        String kind = request.getEventKind();
        if (!StringUtils.hasText(kind) || !KINDS.contains(kind.trim().toUpperCase(Locale.ROOT))) {
            throw new IllegalArgumentException("유형은 GENERAL(일정) 또는 BIRTHDAY(생일)만 가능합니다.");
        }
        kind = kind.trim().toUpperCase(Locale.ROOT);
        request.setEventKind(kind);

        if (!StringUtils.hasText(request.getTitle()) || request.getTitle().trim().length() > 500) {
            throw new IllegalArgumentException("제목은 1~500자로 입력해주세요.");
        }
        request.setTitle(request.getTitle().trim());

        if (StringUtils.hasText(request.getCategoryCode())) {
            request.setCategoryCode(request.getCategoryCode().trim());
        } else {
            request.setCategoryCode(null);
        }

        normalizeEventColorField(request);

        if ("GENERAL".equals(kind)) {
            if (!StringUtils.hasText(request.getStartDate()) || !StringUtils.hasText(request.getEndDate())) {
                throw new IllegalArgumentException("일정의 시작일·종료일을 입력해주세요.");
            }
            LocalDate s = LocalDate.parse(request.getStartDate().trim());
            LocalDate e = LocalDate.parse(request.getEndDate().trim());
            if (s.isAfter(e)) {
                throw new IllegalArgumentException("시작일이 종료일보다 늦을 수 없습니다.");
            }
            request.setStartDate(s.toString());
            request.setEndDate(e.toString());
            request.setStartTime(normalizeTimeForDb(request.getStartTime()));
            request.setEndTime(normalizeTimeForDb(request.getEndTime()));
            if (hasAnyTime(request)) {
                resolveGeneralBounds(s, e, request.getStartTime(), request.getEndTime());
            }
            request.setBirthMonth(null);
            request.setBirthDay(null);
        } else {
            if (request.getBirthMonth() == null || request.getBirthDay() == null) {
                throw new IllegalArgumentException("생일 월·일을 입력해주세요.");
            }
            int bm = request.getBirthMonth();
            int bd = request.getBirthDay();
            if (bm < 1 || bm > 12 || bd < 1 || bd > 31) {
                throw new IllegalArgumentException("생일 월·일이 올바르지 않습니다.");
            }
            try {
                LocalDate.of(2000, bm, bd);
            } catch (DateTimeException ex) {
                throw new IllegalArgumentException("생일 월·일이 올바르지 않습니다.");
            }
            request.setStartDate(null);
            request.setEndDate(null);
            request.setStartTime(null);
            request.setEndTime(null);
        }
    }

    private void normalizeEventColorField(CalendarScheduleSaveRequest request) {
        if (!StringUtils.hasText(request.getEventColor())) {
            request.setEventColor(null);
            return;
        }
        String c = request.getEventColor().trim();
        if (!HEX_COLOR.matcher(c).matches()) {
            throw new IllegalArgumentException("색상은 #RRGGBB 또는 #RGB 형식이어야 합니다.");
        }
        request.setEventColor(c);
    }
}
