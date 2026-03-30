package com.noonoo.prjtbackend.member.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 기존 DB에 member_streamer_profile 이 없을 수 있음 — 부팅 시 CREATE IF NOT EXISTS.
 */
@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class MemberStreamerProfileTableMigration implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        try {
            jdbcTemplate.execute(
                    """
                            CREATE TABLE IF NOT EXISTS member_streamer_profile (
                                member_streamer_profile_seq BIGINT AUTO_INCREMENT PRIMARY KEY,
                                member_seq BIGINT NOT NULL,
                                instagram_url VARCHAR(500) NULL COMMENT '인스타그램 URL',
                                youtube_url VARCHAR(500) NULL COMMENT '유튜브 URL',
                                soop_channel_url VARCHAR(500) NULL COMMENT 'SOOP 방송국 URL',
                                company_category_code VARCHAR(100) NULL COMMENT '속한 컴퍼니/팀 공통코드 code_value',
                                blood_type VARCHAR(10) NULL COMMENT '혈액형',
                                career_history LONGTEXT NULL COMMENT '약력·이력',
                                create_dt DATETIME NULL,
                                modify_dt DATETIME NULL,
                                UNIQUE KEY uk_msp_member (member_seq),
                                CONSTRAINT fk_msp_member FOREIGN KEY (member_seq) REFERENCES member (member_seq) ON DELETE CASCADE
                            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                            """);
            log.info("DB: member_streamer_profile 테이블 확인·생성");
        } catch (Exception e) {
            log.warn("member_streamer_profile 자동 생성 실패 (수동 CREATE 필요할 수 있음): {}", e.toString());
        }
    }
}
