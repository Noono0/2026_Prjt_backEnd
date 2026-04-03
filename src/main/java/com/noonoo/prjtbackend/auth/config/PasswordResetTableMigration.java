package com.noonoo.prjtbackend.auth.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(4)
@RequiredArgsConstructor
public class PasswordResetTableMigration implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        try {
            jdbcTemplate.execute(
                    """
                            CREATE TABLE IF NOT EXISTS password_reset_request (
                                request_seq BIGINT AUTO_INCREMENT PRIMARY KEY,
                                member_seq BIGINT NOT NULL,
                                code_hash VARCHAR(100) NOT NULL,
                                code_expires_at DATETIME NOT NULL,
                                reset_token VARCHAR(64) NULL,
                                reset_token_expires_at DATETIME NULL,
                                used_at DATETIME NULL,
                                create_ip VARCHAR(100) NULL,
                                create_dt DATETIME NULL,
                                KEY idx_prr_member (member_seq),
                                KEY idx_prr_token (reset_token),
                                CONSTRAINT fk_prr_member FOREIGN KEY (member_seq) REFERENCES member (member_seq) ON DELETE CASCADE
                            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                            """);
            log.info("DB: password_reset_request 테이블 확인·생성");
        } catch (Exception e) {
            log.warn("password_reset_request 자동 생성 실패: {}", e.toString());
        }
    }
}
