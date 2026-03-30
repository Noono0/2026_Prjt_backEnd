package com.noonoo.prjtbackend.calendarschedule.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 기존 DB에 calendar_schedule 확장 컬럼이 없을 수 있음(data.sql은 CREATE IF NOT EXISTS만 수행).
 * 부팅 시 누락 컬럼을 ALTER 로 추가한다.
 */
@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class CalendarScheduleCategoryColumnMigration implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        try {
            if (!columnExists("category_code")) {
                jdbcTemplate.execute(
                        """
                                ALTER TABLE calendar_schedule
                                    ADD COLUMN category_code VARCHAR(100) NULL COMMENT '공통코드 그룹 A0003 code_value' AFTER title
                                """);
                log.info("DB 마이그레이션: calendar_schedule.category_code 추가");
            }
            if (!columnExists("start_time")) {
                jdbcTemplate.execute(
                        """
                                ALTER TABLE calendar_schedule
                                    ADD COLUMN start_time TIME NULL COMMENT '시작 시각(선택, 일정만)' AFTER end_date
                                """);
                log.info("DB 마이그레이션: calendar_schedule.start_time 추가");
            }
            if (!columnExists("end_time")) {
                jdbcTemplate.execute(
                        """
                                ALTER TABLE calendar_schedule
                                    ADD COLUMN end_time TIME NULL COMMENT '종료 시각(선택, 일정만)' AFTER start_time
                                """);
                log.info("DB 마이그레이션: calendar_schedule.end_time 추가");
            }
            if (!columnExists("event_color")) {
                jdbcTemplate.execute(
                        """
                                ALTER TABLE calendar_schedule
                                    ADD COLUMN event_color VARCHAR(20) NULL COMMENT '달력 표시용 #RGB 또는 #RRGGBB' AFTER category_code
                                """);
                log.info("DB 마이그레이션: calendar_schedule.event_color 추가");
            }
        } catch (Exception e) {
            log.warn("calendar_schedule 스키마 자동 보정 실패 (수동 ALTER 필요할 수 있음): {}", e.toString());
        }
    }

    private boolean columnExists(String columnName) {
        Integer count = jdbcTemplate.queryForObject(
                """
                        SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
                        WHERE TABLE_SCHEMA = DATABASE()
                          AND TABLE_NAME = 'calendar_schedule'
                          AND COLUMN_NAME = ?
                        """,
                Integer.class,
                columnName);
        return count != null && count > 0;
    }
}
