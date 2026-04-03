package com.noonoo.prjtbackend.analytics.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(3)
@RequiredArgsConstructor
public class VisitorAnalyticsTableMigration implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        try {
            jdbcTemplate.execute(
                    """
                            CREATE TABLE IF NOT EXISTS visitor_heartbeat (
                                visitor_key VARCHAR(128) PRIMARY KEY,
                                member_seq BIGINT NULL,
                                client_ip VARCHAR(100) NULL,
                                user_agent VARCHAR(500) NULL,
                                first_seen_at DATETIME NOT NULL,
                                last_seen_at DATETIME NOT NULL,
                                create_dt DATETIME NULL,
                                modify_dt DATETIME NULL
                            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                            """);

            jdbcTemplate.execute(
                    """
                            CREATE TABLE IF NOT EXISTS visitor_daily_unique (
                                visit_date DATE NOT NULL,
                                visitor_key VARCHAR(128) NOT NULL,
                                created_at DATETIME NOT NULL,
                                PRIMARY KEY (visit_date, visitor_key),
                                KEY idx_vdu_visitor_key (visitor_key)
                            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                            """);

            log.info("DB: visitor analytics tables 확인·생성");
        } catch (Exception e) {
            log.warn("visitor analytics table 자동 생성 실패: {}", e.toString());
        }
    }
}
