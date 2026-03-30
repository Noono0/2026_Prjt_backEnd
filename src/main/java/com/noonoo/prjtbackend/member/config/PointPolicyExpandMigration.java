package com.noonoo.prjtbackend.member.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 기존 DB에 cap_int·nullable threshold 추가 및 전체 정책 행 시드.
 */
@Slf4j
@Component
@Order(7)
@RequiredArgsConstructor
public class PointPolicyExpandMigration implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        try {
            jdbcTemplate.execute(
                    """
                            ALTER TABLE point_policy_setting
                            ADD COLUMN cap_int INT NULL COMMENT '댓글 추가적립 게시글당 합산 상한 등' AFTER reward_points
                            """
            );
            log.info("DB: point_policy_setting.cap_int 추가");
        } catch (Exception e) {
            if (!e.getMessage().contains("Duplicate column")) {
                log.debug("cap_int 추가 스킵 또는 이미 존재: {}", e.getMessage());
            }
        }
        try {
            jdbcTemplate.execute(
                    """
                            ALTER TABLE point_policy_setting
                            MODIFY COLUMN threshold_int INT NULL COMMENT '임계값(추천 수 등)'
                            """
            );
        } catch (Exception e) {
            log.debug("threshold_int 수정 스킵: {}", e.getMessage());
        }
        try {
            jdbcTemplate.execute(
                    """
                            ALTER TABLE point_policy_setting
                            MODIFY COLUMN reward_points BIGINT NULL COMMENT '보상 포인트'
                            """
            );
        } catch (Exception e) {
            log.debug("reward_points 수정 스킵: {}", e.getMessage());
        }

        String[] seeds = {
                "INSERT IGNORE INTO point_policy_setting (policy_key, use_yn, threshold_int, reward_points, cap_int, modify_dt) VALUES ('SIGNUP', 'Y', NULL, 100, NULL, NOW())",
                "INSERT IGNORE INTO point_policy_setting (policy_key, use_yn, threshold_int, reward_points, cap_int, modify_dt) VALUES ('FREE_BOARD_POST', 'Y', NULL, 10, NULL, NOW())",
                "INSERT IGNORE INTO point_policy_setting (policy_key, use_yn, threshold_int, reward_points, cap_int, modify_dt) VALUES ('BOARD_COMMENT_FIRST', 'Y', NULL, 10, NULL, NOW())",
                "INSERT IGNORE INTO point_policy_setting (policy_key, use_yn, threshold_int, reward_points, cap_int, modify_dt) VALUES ('BOARD_COMMENT_EXTRA', 'Y', NULL, NULL, 5, NOW())",
                "INSERT IGNORE INTO point_policy_setting (policy_key, use_yn, threshold_int, reward_points, cap_int, modify_dt) VALUES ('NOTICE_COMMENT_FIRST', 'Y', NULL, 10, NULL, NOW())",
                "INSERT IGNORE INTO point_policy_setting (policy_key, use_yn, threshold_int, reward_points, cap_int, modify_dt) VALUES ('NOTICE_COMMENT_EXTRA', 'Y', NULL, NULL, 5, NOW())",
                "INSERT IGNORE INTO point_policy_setting (policy_key, use_yn, threshold_int, reward_points, cap_int, modify_dt) VALUES ('FREE_BOARD_LIKE', 'Y', 50, 100, NULL, NOW())",
        };
        for (String sql : seeds) {
            try {
                jdbcTemplate.update(sql);
            } catch (Exception e) {
                log.warn("정책 시드 실패: {}", e.getMessage());
            }
        }
        log.info("DB: 포인트 정책 행 시드 완료");
    }
}
