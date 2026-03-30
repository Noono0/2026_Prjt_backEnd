-- 기존 calendar_schedule 테이블 확장 (이미 있으면 1060 오류 → 해당 줄만 스킵)
-- 사용: mysql -u ... -p ... DB명 < alter-calendar-schedule-category-code.sql

ALTER TABLE calendar_schedule
    ADD COLUMN category_code VARCHAR(100) NULL COMMENT '공통코드 그룹 A0003 code_value' AFTER title;

ALTER TABLE calendar_schedule
    ADD COLUMN start_time TIME NULL COMMENT '시작 시각(선택, 일정만)' AFTER end_date;

ALTER TABLE calendar_schedule
    ADD COLUMN end_time TIME NULL COMMENT '종료 시각(선택, 일정만)' AFTER start_time;
