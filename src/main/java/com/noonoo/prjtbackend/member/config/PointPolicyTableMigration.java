package com.noonoo.prjtbackend.member.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(6)
@RequiredArgsConstructor
public class PointPolicyTableMigration implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        try {
            jdbcTemplate.execute(
                    """
                            CREATE TABLE IF NOT EXISTS point_policy_setting (
                                policy_key VARCHAR(64) NOT NULL PRIMARY KEY COMMENT 'SIGNUP, FREE_BOARD_POST 등',
                                use_yn CHAR(1) NOT NULL DEFAULT 'Y',
                                threshold_int INT NULL COMMENT '임계값(추천 수 등)',
                                reward_points BIGINT NULL COMMENT '보상 포인트',
                                cap_int INT NULL COMMENT '댓글 추가적립 게시글당 합산 상한 등',
                                modify_dt DATETIME NULL
                            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                            """
            );
            jdbcTemplate.execute(
                    """
                            CREATE TABLE IF NOT EXISTS board_like_milestone_granted (
                                board_seq BIGINT NOT NULL PRIMARY KEY,
                                writer_member_seq BIGINT NOT NULL,
                                reward_points BIGINT NOT NULL,
                                granted_dt DATETIME NOT NULL,
                                KEY idx_blmg_writer (writer_member_seq)
                            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                            """
            );
            jdbcTemplate.update(
                    """
                            INSERT IGNORE INTO point_policy_setting (policy_key, use_yn, threshold_int, reward_points, cap_int, modify_dt)
                            VALUES ('FREE_BOARD_LIKE', 'Y', 50, 100, NULL, NOW())
                            """
            );
            log.info("DB: point_policy_setting / board_like_milestone_granted 확인·시드");
        } catch (Exception e) {
            log.warn("포인트 정책 테이블 자동 생성 실패: {}", e.toString());
        }
    }
}
