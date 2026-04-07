package com.noonoo.prjtbackend.member.serviceImpl;

import com.noonoo.prjtbackend.member.PointRankingReasonLabels;
import com.noonoo.prjtbackend.member.dto.PointRankingBreakdownRowDto;
import com.noonoo.prjtbackend.member.dto.PointRankingEntryDto;
import com.noonoo.prjtbackend.member.dto.PointRankingReasonBreakdownDto;
import com.noonoo.prjtbackend.member.dto.PointRankingRowDto;
import com.noonoo.prjtbackend.member.mapper.MemberWalletMapper;
import com.noonoo.prjtbackend.member.service.PointRankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PointRankingServiceImpl implements PointRankingService {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final MemberWalletMapper memberWalletMapper;

    @Override
    public List<PointRankingEntryDto> ranking(String period) {
        String p = period != null ? period.trim().toUpperCase(Locale.ROOT) : "DAY";
        LocalDate today = LocalDate.now(KST);
        LocalDate fromDate;
        LocalDate toExclusiveDate;
        switch (p) {
            case "WEEK" -> {
                fromDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                toExclusiveDate = fromDate.plusWeeks(1);
            }
            case "MONTH" -> {
                fromDate = today.withDayOfMonth(1);
                toExclusiveDate = fromDate.plusMonths(1);
            }
            default -> {
                fromDate = today;
                toExclusiveDate = today.plusDays(1);
            }
        }
        String fromInclusive = fromDate.atStartOfDay().format(DT);
        String toExclusive = toExclusiveDate.atStartOfDay().format(DT);

        List<PointRankingRowDto> rows = memberWalletMapper.selectPointRanking(fromInclusive, toExclusive);
        if (rows.isEmpty()) {
            return List.of();
        }
        List<Long> memberSeqs = rows.stream().map(PointRankingRowDto::getMemberSeq).toList();
        List<PointRankingBreakdownRowDto> breakdownRows =
                memberWalletMapper.selectPointRankingBreakdown(fromInclusive, toExclusive, memberSeqs);
        Map<Long, List<PointRankingBreakdownRowDto>> byMember = new LinkedHashMap<>();
        for (PointRankingBreakdownRowDto b : breakdownRows) {
            byMember.computeIfAbsent(b.getMemberSeq(), k -> new ArrayList<>()).add(b);
        }

        List<PointRankingEntryDto> out = new ArrayList<>();
        int rank = 1;
        for (PointRankingRowDto r : rows) {
            List<PointRankingReasonBreakdownDto> breakdown = byMember.getOrDefault(r.getMemberSeq(), List.of()).stream()
                    .map(br -> PointRankingReasonBreakdownDto.builder()
                            .reasonCode(br.getReasonCode())
                            .reasonLabel(PointRankingReasonLabels.label(br.getReasonCode()))
                            .points(br.getPointsEarned())
                            .build())
                    .toList();
            out.add(PointRankingEntryDto.builder()
                    .rank(rank++)
                    .memberSeq(r.getMemberSeq())
                    .memberId(r.getMemberId())
                    .displayLabel(r.getDisplayLabel())
                    .pointsEarned(r.getPointsEarned())
                    .breakdown(breakdown)
                    .build());
        }
        return out;
    }
}
