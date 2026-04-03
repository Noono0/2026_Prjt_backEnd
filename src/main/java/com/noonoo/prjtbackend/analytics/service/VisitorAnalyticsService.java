package com.noonoo.prjtbackend.analytics.service;

import com.noonoo.prjtbackend.analytics.dto.VisitorCountPointDto;
import com.noonoo.prjtbackend.analytics.dto.VisitorOverviewDto;
import com.noonoo.prjtbackend.common.config.RequestContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VisitorAnalyticsService {

    private final JdbcTemplate jdbcTemplate;

    @Value("${app.analytics.heartbeat-ttl-seconds:300}")
    private int heartbeatTtlSeconds;

    @Transactional
    public void heartbeat(String visitorKey, String userAgent) {
        String key = normalizeVisitorKey(visitorKey);
        if (!StringUtils.hasText(key)) {
            throw new IllegalArgumentException("visitorKey 가 필요합니다.");
        }

        Long memberSeq = RequestContext.getLoginMemberSeq();
        String clientIp = RequestContext.getClientIp();
        LocalDateTime now = LocalDateTime.now();
        Timestamp ts = Timestamp.valueOf(now);

        jdbcTemplate.update(
                """
                        INSERT INTO visitor_heartbeat (
                            visitor_key, member_seq, client_ip, user_agent,
                            first_seen_at, last_seen_at, create_dt, modify_dt
                        )
                        VALUES (?, ?, ?, ?, ?, ?, NOW(), NOW())
                        ON DUPLICATE KEY UPDATE
                            member_seq = VALUES(member_seq),
                            client_ip = VALUES(client_ip),
                            user_agent = VALUES(user_agent),
                            last_seen_at = VALUES(last_seen_at),
                            modify_dt = NOW()
                        """,
                key,
                memberSeq,
                safeLimit(clientIp, 100),
                safeLimit(userAgent, 500),
                ts,
                ts
        );

        jdbcTemplate.update(
                """
                        INSERT IGNORE INTO visitor_daily_unique (visit_date, visitor_key, created_at)
                        VALUES (?, ?, NOW())
                        """,
                LocalDate.now(),
                key
        );
    }

    @Transactional(readOnly = true)
    public VisitorOverviewDto overview(int days, int weeks, int months) {
        int safeDays = Math.min(Math.max(days, 1), 180);
        int safeWeeks = Math.min(Math.max(weeks, 1), 104);
        int safeMonths = Math.min(Math.max(months, 1), 36);

        Long online = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(DISTINCT
                            CASE
                                WHEN member_seq IS NOT NULL THEN CONCAT('M:', member_seq)
                                ELSE CONCAT('V:', visitor_key)
                            END
                        ) FROM visitor_heartbeat
                        WHERE last_seen_at >= DATE_SUB(NOW(), INTERVAL ? SECOND)
                        """,
                Long.class,
                heartbeatTtlSeconds
        );

        List<VisitorCountPointDto> daily = jdbcTemplate.query(
                """
                        SELECT DATE_FORMAT(visit_date, '%Y-%m-%d') AS label,
                               COUNT(*) AS visitors
                        FROM visitor_daily_unique
                        WHERE visit_date >= DATE_SUB(CURDATE(), INTERVAL ? DAY)
                        GROUP BY visit_date
                        ORDER BY visit_date
                        """,
                (rs, rowNum) -> VisitorCountPointDto.builder()
                        .label(rs.getString("label"))
                        .visitors(rs.getLong("visitors"))
                        .build(),
                safeDays - 1
        );

        List<VisitorCountPointDto> weekly = jdbcTemplate.query(
                """
                        SELECT DATE_FORMAT(DATE_SUB(visit_date, INTERVAL WEEKDAY(visit_date) DAY), '%Y-%m-%d') AS label,
                               COUNT(DISTINCT visitor_key) AS visitors
                        FROM visitor_daily_unique
                        WHERE visit_date >= DATE_SUB(CURDATE(), INTERVAL ? WEEK)
                        GROUP BY YEARWEEK(visit_date, 1)
                        ORDER BY MIN(visit_date)
                        """,
                (rs, rowNum) -> VisitorCountPointDto.builder()
                        .label(rs.getString("label"))
                        .visitors(rs.getLong("visitors"))
                        .build(),
                safeWeeks - 1
        );

        List<VisitorCountPointDto> monthly = jdbcTemplate.query(
                """
                        SELECT DATE_FORMAT(visit_date, '%Y-%m') AS label,
                               COUNT(DISTINCT visitor_key) AS visitors
                        FROM visitor_daily_unique
                        WHERE visit_date >= DATE_SUB(CURDATE(), INTERVAL ? MONTH)
                        GROUP BY DATE_FORMAT(visit_date, '%Y-%m')
                        ORDER BY DATE_FORMAT(visit_date, '%Y-%m')
                        """,
                (rs, rowNum) -> VisitorCountPointDto.builder()
                        .label(rs.getString("label"))
                        .visitors(rs.getLong("visitors"))
                        .build(),
                safeMonths - 1
        );

        return VisitorOverviewDto.builder()
                .onlineCount(online == null ? 0L : online)
                .heartbeatTtlSeconds(heartbeatTtlSeconds)
                .daily(daily)
                .weekly(weekly)
                .monthly(monthly)
                .build();
    }

    private static String normalizeVisitorKey(String visitorKey) {
        if (!StringUtils.hasText(visitorKey)) {
            return "";
        }
        return safeLimit(visitorKey.trim(), 128);
    }

    private static String safeLimit(String value, int maxLen) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        String s = value.trim();
        return s.length() > maxLen ? s.substring(0, maxLen) : s;
    }
}
