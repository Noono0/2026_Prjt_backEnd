-- 한 이벤트에 같은 회원이 여러 번 베팅(행 여러 개)하려면
-- uk_ebb_event_member (event_battle_seq, member_seq) UNIQUE 가 없어야 합니다.
-- data.sql 또는 migrate-event-battle-existing-db.sql 에서 제거하지만,
-- 이미 UNIQUE 가 남아 있으면 아래를 한 번 실행하세요.

SET @db := DATABASE();
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
