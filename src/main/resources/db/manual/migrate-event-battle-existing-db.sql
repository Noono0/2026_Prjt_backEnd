-- =============================================================================
-- 이벤트 대결: 예전 스키마(좌/우 컬럼 등) → 옵션 테이블 + winner_option_seq
-- 백업 후 MySQL/MariaDB 클라이언트에서 한 번에 실행하세요.
--
-- 증상: Unknown column 'e.winner_option_seq' — 기존 DB에 CREATE TABLE IF NOT EXISTS만
--       적용돼 컬럼이 없는 경우. 이 스크립트로 ALTER + (선택) 데이터 이전.
-- =============================================================================

SET @db := DATABASE();

-- 1) 주제 옵션 테이블
CREATE TABLE IF NOT EXISTS event_battle_option (
    event_battle_option_seq BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_battle_seq        BIGINT      NOT NULL,
    sort_order              TINYINT     NOT NULL COMMENT '1~5',
    label                   VARCHAR(200) NOT NULL,
    points_total            BIGINT      NOT NULL DEFAULT 0,
    UNIQUE KEY uk_ebo_event_sort (event_battle_seq, sort_order),
    KEY idx_ebo_event (event_battle_seq),
    CONSTRAINT fk_ebo_event FOREIGN KEY (event_battle_seq) REFERENCES event_battle (event_battle_seq) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2) event_battle.winner_option_seq 추가 (없을 때만)
SET @sql := (
    SELECT IF(
        (SELECT COUNT(*) FROM information_schema.COLUMNS
         WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'event_battle' AND COLUMN_NAME = 'winner_option_seq') = 0,
        'ALTER TABLE event_battle ADD COLUMN winner_option_seq BIGINT NULL COMMENT ''event_battle_option PK'' AFTER status',
        'SELECT 1'
    )
);
PREPARE _stmt FROM @sql;
EXECUTE _stmt;
DEALLOCATE PREPARE _stmt;

-- 3) 예전 left_label / right_label → event_battle_option (해당 컬럼이 있을 때만 동적 실행)
SET @has_left := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'event_battle' AND COLUMN_NAME = 'left_label');
SET @has_lpt := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'event_battle' AND COLUMN_NAME = 'left_points_total');
SET @pts1 := IF(@has_lpt > 0, 'IFNULL(e.left_points_total, 0)', '0');
SET @sql := IF(@has_left > 0,
    CONCAT(
        'INSERT INTO event_battle_option (event_battle_seq, sort_order, label, points_total) ',
        'SELECT e.event_battle_seq, 1, e.left_label, ', @pts1, ' ',
        'FROM event_battle e ',
        'WHERE NOT EXISTS (SELECT 1 FROM event_battle_option o WHERE o.event_battle_seq = e.event_battle_seq AND o.sort_order = 1)'
    ),
    'SELECT 1');
PREPARE _stmt FROM @sql;
EXECUTE _stmt;
DEALLOCATE PREPARE _stmt;

SET @has_right := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'event_battle' AND COLUMN_NAME = 'right_label');
SET @has_rpt := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'event_battle' AND COLUMN_NAME = 'right_points_total');
SET @pts2 := IF(@has_rpt > 0, 'IFNULL(e.right_points_total, 0)', '0');
SET @sql := IF(@has_right > 0,
    CONCAT(
        'INSERT INTO event_battle_option (event_battle_seq, sort_order, label, points_total) ',
        'SELECT e.event_battle_seq, 2, e.right_label, ', @pts2, ' ',
        'FROM event_battle e ',
        'WHERE NOT EXISTS (SELECT 1 FROM event_battle_option o WHERE o.event_battle_seq = e.event_battle_seq AND o.sort_order = 2)'
    ),
    'SELECT 1');
PREPARE _stmt FROM @sql;
EXECUTE _stmt;
DEALLOCATE PREPARE _stmt;

-- 4) 예전 winner_side → winner_option_seq
SET @has_ws := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'event_battle' AND COLUMN_NAME = 'winner_side');
SET @sql := IF(@has_ws > 0,
    'UPDATE event_battle e INNER JOIN event_battle_option o ON o.event_battle_seq = e.event_battle_seq AND o.sort_order = 1 SET e.winner_option_seq = o.event_battle_option_seq WHERE e.winner_side = ''LEFT'' AND e.winner_option_seq IS NULL',
    'SELECT 1');
PREPARE _stmt FROM @sql;
EXECUTE _stmt;
DEALLOCATE PREPARE _stmt;

SET @sql := IF(@has_ws > 0,
    'UPDATE event_battle e INNER JOIN event_battle_option o ON o.event_battle_seq = e.event_battle_seq AND o.sort_order = 2 SET e.winner_option_seq = o.event_battle_option_seq WHERE e.winner_side = ''RIGHT'' AND e.winner_option_seq IS NULL',
    'SELECT 1');
PREPARE _stmt FROM @sql;
EXECUTE _stmt;
DEALLOCATE PREPARE _stmt;

-- 5) event_battle_bet: side → event_battle_option_seq
SET @sql := (
    SELECT IF(
        (SELECT COUNT(*) FROM information_schema.COLUMNS
         WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'event_battle_bet' AND COLUMN_NAME = 'event_battle_option_seq') = 0
        AND (SELECT COUNT(*) FROM information_schema.COLUMNS
             WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'event_battle_bet' AND COLUMN_NAME = 'side') > 0,
        'ALTER TABLE event_battle_bet ADD COLUMN event_battle_option_seq BIGINT NULL AFTER event_battle_seq',
        'SELECT 1'
    )
);
PREPARE _stmt FROM @sql;
EXECUTE _stmt;
DEALLOCATE PREPARE _stmt;

SET @has_side := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'event_battle_bet' AND COLUMN_NAME = 'side');
SET @sql := IF(@has_side > 0,
    'UPDATE event_battle_bet b INNER JOIN event_battle_option o ON o.event_battle_seq = b.event_battle_seq AND o.sort_order = 1 SET b.event_battle_option_seq = o.event_battle_option_seq WHERE b.side = ''LEFT'' AND b.event_battle_option_seq IS NULL',
    'SELECT 1');
PREPARE _stmt FROM @sql;
EXECUTE _stmt;
DEALLOCATE PREPARE _stmt;

SET @sql := IF(@has_side > 0,
    'UPDATE event_battle_bet b INNER JOIN event_battle_option o ON o.event_battle_seq = b.event_battle_seq AND o.sort_order = 2 SET b.event_battle_option_seq = o.event_battle_option_seq WHERE b.side = ''RIGHT'' AND b.event_battle_option_seq IS NULL',
    'SELECT 1');
PREPARE _stmt FROM @sql;
EXECUTE _stmt;
DEALLOCATE PREPARE _stmt;

-- 6) event_battle_option_seq 가 NULL 없을 때만 NOT NULL 로 고정 (NULL 남으면 스크립트는 건너뜀 → 데이터 수동 정리)
SET @has_ebb_opt := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'event_battle_bet' AND COLUMN_NAME = 'event_battle_option_seq');
SET @ebb_nulls := IF(@has_ebb_opt = 0, 0, (SELECT COUNT(*) FROM event_battle_bet WHERE event_battle_option_seq IS NULL));
SET @sql := (
    SELECT IF(
        @ebb_nulls = 0
        AND (SELECT COUNT(*) FROM information_schema.COLUMNS
             WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'event_battle_bet'
               AND COLUMN_NAME = 'event_battle_option_seq' AND IS_NULLABLE = 'YES') > 0,
        'ALTER TABLE event_battle_bet MODIFY COLUMN event_battle_option_seq BIGINT NOT NULL',
        'SELECT 1'
    )
);
PREPARE _stmt FROM @sql;
EXECUTE _stmt;
DEALLOCATE PREPARE _stmt;

-- 6b) 베팅 실행마다 1행 INSERT → (event_battle_seq, member_seq) UNIQUE 는 사용하지 않음.
--     과거 스크립트가 uk_ebb_event_member 를 추가했을 수 있으므로 제거하고 비-unique 인덱스만 둔다.
SET @sql := (
    SELECT IF(
        (SELECT COUNT(*) FROM information_schema.STATISTICS
         WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'event_battle_bet' AND INDEX_NAME = 'uk_ebb_event_member') > 0,
        'ALTER TABLE event_battle_bet DROP INDEX uk_ebb_event_member',
        'SELECT 1'
    )
);
PREPARE _stmt FROM @sql;
EXECUTE _stmt;
DEALLOCATE PREPARE _stmt;

SET @sql := (
    SELECT IF(
        (SELECT COUNT(*) FROM information_schema.STATISTICS
         WHERE TABLE_SCHEMA = @db AND TABLE_NAME = 'event_battle_bet' AND INDEX_NAME = 'idx_ebb_event_member') = 0,
        'ALTER TABLE event_battle_bet ADD INDEX idx_ebb_event_member (event_battle_seq, member_seq)',
        'SELECT 1'
    )
);
PREPARE _stmt FROM @sql;
EXECUTE _stmt;
DEALLOCATE PREPARE _stmt;

-- 7) FK (이미 있으면 생략)
SET @sql := (
    SELECT IF(
        (SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS
         WHERE CONSTRAINT_SCHEMA = @db AND TABLE_NAME = 'event_battle_bet' AND CONSTRAINT_NAME = 'fk_ebb_option') = 0,
        'ALTER TABLE event_battle_bet ADD CONSTRAINT fk_ebb_option FOREIGN KEY (event_battle_option_seq) REFERENCES event_battle_option (event_battle_option_seq)',
        'SELECT 1'
    )
);
PREPARE _stmt FROM @sql;
EXECUTE _stmt;
DEALLOCATE PREPARE _stmt;

-- 8) 구 컬럼 제거(선택). 검증 후 주석 해제.
-- ALTER TABLE event_battle DROP COLUMN left_label, DROP COLUMN right_label, DROP COLUMN left_points_total, DROP COLUMN right_points_total, DROP COLUMN winner_side;
-- ALTER TABLE event_battle_bet DROP COLUMN side;
