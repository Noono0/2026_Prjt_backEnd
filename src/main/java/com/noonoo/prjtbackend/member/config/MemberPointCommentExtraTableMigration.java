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
@Order(4)
@RequiredArgsConstructor
public class MemberPointCommentExtraTableMigration implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        try {
            jdbcTemplate.execute(
                    """
                            CREATE TABLE IF NOT EXISTS member_point_comment_extra (
                                member_seq BIGINT NOT NULL,
                                post_type VARCHAR(16) NOT NULL COMMENT 'BOARD | NOTICE',
                                post_seq BIGINT NOT NULL,
                                extra_points_earned INT NOT NULL DEFAULT 0 COMMENT '첫 댓글 이후 같은 글에서 추가 적립 합(상한 5)',
                                modify_dt DATETIME NULL,
                                PRIMARY KEY (member_seq, post_type, post_seq),
                                KEY idx_mpce_post (post_type, post_seq)
                            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                            """
            );
            log.info("DB: member_point_comment_extra 확인·생성");
        } catch (Exception e) {
            log.warn("member_point_comment_extra 테이블 자동 생성 실패: {}", e.toString());
        }
    }
}
