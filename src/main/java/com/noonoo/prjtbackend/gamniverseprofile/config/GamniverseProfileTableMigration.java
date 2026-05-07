package com.noonoo.prjtbackend.gamniverseprofile.config;

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
public class GamniverseProfileTableMigration implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        try {
            jdbcTemplate.execute(
                    """
                            CREATE TABLE IF NOT EXISTS gamniverse_profile (
                                gamniverse_profile_seq BIGINT AUTO_INCREMENT PRIMARY KEY,
                                profile_name VARCHAR(100) NOT NULL COMMENT '노출 이름',
                                sort_order INT NOT NULL DEFAULT 0 COMMENT '노출 순서',
                                rank_code VARCHAR(30) NOT NULL DEFAULT 'IRON' COMMENT 'DIAMOND|GOLD|SILVER|IRON|WOOD',
                                affiliation_code VARCHAR(100) NULL COMMENT 'A0004 중분류 code_value',
                                broadcast_link VARCHAR(500) NULL COMMENT '방송국 링크',
                                soop_broadcast_link VARCHAR(500) NULL COMMENT '숲 라이브 체크 링크',
                                instagram_url VARCHAR(500) NULL COMMENT '인스타 링크',
                                youtube_url VARCHAR(500) NULL COMMENT '유튜브 링크',
                                cafe_link VARCHAR(500) NULL COMMENT '네이버 카페 링크',
                                profile_image_file_seq BIGINT NULL COMMENT 'attach_file.file_seq',
                                profile_rows_json LONGTEXT NULL COMMENT '프로필 행 JSON',
                                use_yn CHAR(1) NOT NULL DEFAULT 'Y',
                                create_dt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                modify_dt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                            """);
            ensureCafeLinkColumn();
            ensureSoopBroadcastLinkColumn();
            log.info("DB: gamniverse_profile 테이블 확인·생성");
        } catch (Exception e) {
            log.warn("gamniverse_profile 자동 생성 실패: {}", e.toString());
        }
    }

    /** 기존 DB에는 CREATE IF NOT EXISTS 가 스키마를 바꾸지 않으므로 컬럼만 보강 */
    private void ensureCafeLinkColumn() {
        try {
            Integer count =
                    jdbcTemplate.queryForObject(
                            """
                            SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
                            WHERE TABLE_SCHEMA = DATABASE()
                              AND TABLE_NAME = 'gamniverse_profile'
                              AND COLUMN_NAME = 'cafe_link'
                            """,
                            Integer.class);
            if (count != null && count == 0) {
                jdbcTemplate.execute(
                        """
                                ALTER TABLE gamniverse_profile
                                    ADD COLUMN cafe_link VARCHAR(500) NULL COMMENT '네이버 카페 링크' AFTER youtube_url
                                """);
                log.info("DB 마이그레이션: gamniverse_profile.cafe_link 추가");
            }
        } catch (Exception e) {
            log.warn("gamniverse_profile.cafe_link 컬럼 보강 실패: {}", e.toString());
        }
    }

    private void ensureSoopBroadcastLinkColumn() {
        try {
            Integer count =
                    jdbcTemplate.queryForObject(
                            """
                            SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
                            WHERE TABLE_SCHEMA = DATABASE()
                              AND TABLE_NAME = 'gamniverse_profile'
                              AND COLUMN_NAME = 'soop_broadcast_link'
                            """,
                            Integer.class);
            if (count != null && count == 0) {
                jdbcTemplate.execute(
                        """
                                ALTER TABLE gamniverse_profile
                                    ADD COLUMN soop_broadcast_link VARCHAR(500) NULL COMMENT '숲 라이브 체크 링크' AFTER broadcast_link
                                """);
                log.info("DB 마이그레이션: gamniverse_profile.soop_broadcast_link 추가");
            }
        } catch (Exception e) {
            log.warn("gamniverse_profile.soop_broadcast_link 컬럼 보강 실패: {}", e.toString());
        }
    }
}
