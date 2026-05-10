package com.noonoo.prjtbackend.board.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 자유게시판 태그 목록 저장용 컬럼. 기존 DB에 없을 수 있어 부팅 시 ALTER 로 추가한다.
 */
@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class BoardTagColumnMigration implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        try {
            if (!columnExists("tag_list")) {
                jdbcTemplate.execute(
                        """
                                ALTER TABLE board
                                    ADD COLUMN tag_list VARCHAR(500) NULL COMMENT '쉼표 구분 태그(본문 검색용)' AFTER title
                                """);
                log.info("DB 마이그레이션: board.tag_list 추가");
            }
        } catch (Exception e) {
            log.warn("board.tag_list 스키마 자동 보정 실패 (수동 ALTER 필요할 수 있음): {}", e.toString());
        }
    }

    private boolean columnExists(String columnName) {
        Integer count =
                jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
                        WHERE TABLE_SCHEMA = DATABASE()
                          AND TABLE_NAME = 'board'
                          AND COLUMN_NAME = ?
                        """,
                        Integer.class,
                        columnName);
        return count != null && count > 0;
    }
}
