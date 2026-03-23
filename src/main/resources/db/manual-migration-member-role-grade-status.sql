-- =============================================================================
-- 기존 DB에서 MEMBER 단일 ROLE / status 컬럼을 쓰던 경우 수동 마이그레이션 참고용
-- (앱 기동 전 백업 후, 환경에 맞게 주석 해제·수정 후 실행)
-- =============================================================================

-- 1) JPA ddl-auto: update 로 member 테이블에 컬럼이 없으면 추가됨.
--    아래는 예시이며 실제 컬럼명·타입은 운영 스키마에 맞출 것.

-- ALTER TABLE member ADD COLUMN grade_code VARCHAR(50) DEFAULT 'NORMAL';
-- ALTER TABLE member ADD COLUMN status_code VARCHAR(50) DEFAULT 'ACTIVE';

-- 2) 예: 예전 단일 role_code 컬럼이 남아 있을 때 → MEMBER_ROLE 로 이관
-- INSERT IGNORE INTO MEMBER_ROLE (MEMBER_SEQ, ROLE_CODE, CRT_DT, CRT_ID, CRT_IP)
-- SELECT m.member_seq, m.role_code, NOW(), 'MIGRATE', '127.0.0.1'
-- FROM member m
-- WHERE m.role_code IS NOT NULL AND TRIM(m.role_code) <> '';

-- 3) 예: 예전 status 문자열 컬럼이 있을 때 → status_code 로 매핑
-- UPDATE member m
-- SET m.status_code = CASE UPPER(TRIM(m.status))
--     WHEN 'SUSPEND' THEN 'SUSPENDED'
--     WHEN 'ACTIVE' THEN 'ACTIVE'
--     ELSE COALESCE(m.status_code, 'ACTIVE')
--   END
-- WHERE m.status IS NOT NULL;

-- 4) 이관 후 레거시 컬럼 제거(선택, 충분히 검증한 뒤에만)
-- ALTER TABLE member DROP COLUMN role_code;
-- ALTER TABLE member DROP COLUMN status;
