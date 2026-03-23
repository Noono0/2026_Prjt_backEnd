-- 역할-메뉴 매핑 / AuthorityBuilder 와 동일한 MENU_CODE (MenuAuthorities 상수)
-- 최초 1회만 삽입 (MENU_CODE 유니크 기준)
INSERT IGNORE INTO MENU (MENU_CODE, MENU_NAME, MENU_PATH, PARENT_MENU_ID, SORT_ORDER, USE_YN, CRT_DT, UPD_DT)
VALUES
    ('MEMBER', '회원관리', '/members', NULL, 10, 'Y', NOW(), NOW()),
    ('MENU', '메뉴관리', '/menus', NULL, 20, 'Y', NOW(), NOW()),
    ('ROLE', '권한관리', '/roles', NULL, 30, 'Y', NOW(), NOW()),
    ('CODE_GROUP', '코드그룹', '/common-codes', NULL, 40, 'Y', NOW(), NOW()),
    ('CODE_DETAIL', '코드상세', '/common-codes', NULL, 50, 'Y', NOW(), NOW()),
    ('PRODUCT', '상품(샘플)', '/products', NULL, 60, 'Y', NOW(), NOW()),
    ('ORDER', '주문(샘플)', '/orders', NULL, 70, 'Y', NOW(), NOW());

-- 트리 UI 확인용 하위 메뉴 (루트만 있으면 화면이 평면 목록처럼 보임)
INSERT IGNORE INTO MENU (MENU_CODE, MENU_NAME, MENU_PATH, PARENT_MENU_ID, SORT_ORDER, USE_YN, CRT_DT, UPD_DT)
SELECT 'MEMBER_SUB', '회원 하위(샘플)', '/members', m.MENU_ID, 1, 'Y', NOW(), NOW()
FROM MENU m WHERE m.MENU_CODE = 'MEMBER' LIMIT 1;

INSERT IGNORE INTO MENU (MENU_CODE, MENU_NAME, MENU_PATH, PARENT_MENU_ID, SORT_ORDER, USE_YN, CRT_DT, UPD_DT)
SELECT 'MENU_SUB', '메뉴 하위(샘플)', '/menus', m.MENU_ID, 1, 'Y', NOW(), NOW()
FROM MENU m WHERE m.MENU_CODE = 'MENU' LIMIT 1;

INSERT IGNORE INTO MENU (MENU_CODE, MENU_NAME, MENU_PATH, PARENT_MENU_ID, SORT_ORDER, USE_YN, CRT_DT, UPD_DT)
SELECT 'MEMBER_SUB2', '회원 2depth(샘플)', '/members', m.MENU_ID, 1, 'Y', NOW(), NOW()
FROM MENU m WHERE m.MENU_CODE = 'MEMBER_SUB' LIMIT 1;

-- ---------------------------------------------------------------------
-- ROLE (시스템 권한) + MEMBER_ROLE 공통코드와 동일 코드 체계 유지
-- ---------------------------------------------------------------------
INSERT IGNORE INTO ROLE (ROLE_CODE, ROLE_NAME, USE_YN, CRT_DT, UPD_DT) VALUES
    ('USER', '일반회원', 'Y', NOW(), NOW()),
    ('ADMIN', '관리자', 'Y', NOW(), NOW());

-- ---------------------------------------------------------------------
-- 공통코드: MEMBER_ROLE / MEMBER_GRADE / MEMBER_STATUS
-- (code_group / code_detail — JPA 엔티티 테이블명 소문자)
-- ---------------------------------------------------------------------
INSERT IGNORE INTO code_group (code_group_id, code_group_name, description, sort_order, use_yn, create_dt, create_id, create_ip, modify_dt, modify_id, modify_ip, status)
VALUES
    ('MEMBER_ROLE', '회원 시스템 권한(ROLE)', 'ROLE·MEMBER_ROLE과 동일 코드. Spring Security 메뉴 권한에 사용', 110, 'Y', NOW(), 'SYSTEM', '127.0.0.1', NOW(), 'SYSTEM', '127.0.0.1', 'ACTIVE'),
    ('MEMBER_GRADE', '회원 등급', '쇼핑몰 VIP 등 비즈니스 등급(MEMBER.GRADE_CODE)', 120, 'Y', NOW(), 'SYSTEM', '127.0.0.1', NOW(), 'SYSTEM', '127.0.0.1', 'ACTIVE'),
    ('MEMBER_STATUS', '회원 계정 상태', '로그인 가능 여부 등(MEMBER.STATUS_CODE)', 130, 'Y', NOW(), 'SYSTEM', '127.0.0.1', NOW(), 'SYSTEM', '127.0.0.1', 'ACTIVE');

-- MEMBER_ROLE 상세 (ROLE_CODE와 동일)
INSERT INTO code_detail (code_group_seq, parent_detail_seq, code_id, code_value, code_name, code_level, sort_order, use_yn, description, status, create_dt, create_id, create_ip, modify_dt, modify_id, modify_ip)
SELECT g.code_group_seq, NULL, 'USER', 'USER', '일반회원', 1, 10, 'Y', NULL, 'ACTIVE', NOW(), 'SYSTEM', '127.0.0.1', NOW(), 'SYSTEM', '127.0.0.1'
FROM code_group g
WHERE g.code_group_id = 'MEMBER_ROLE'
  AND NOT EXISTS (SELECT 1 FROM code_detail d WHERE d.code_group_seq = g.code_group_seq AND d.code_id = 'USER')
LIMIT 1;

INSERT INTO code_detail (code_group_seq, parent_detail_seq, code_id, code_value, code_name, code_level, sort_order, use_yn, description, status, create_dt, create_id, create_ip, modify_dt, modify_id, modify_ip)
SELECT g.code_group_seq, NULL, 'ADMIN', 'ADMIN', '관리자', 1, 20, 'Y', NULL, 'ACTIVE', NOW(), 'SYSTEM', '127.0.0.1', NOW(), 'SYSTEM', '127.0.0.1'
FROM code_group g
WHERE g.code_group_id = 'MEMBER_ROLE'
  AND NOT EXISTS (SELECT 1 FROM code_detail d WHERE d.code_group_seq = g.code_group_seq AND d.code_id = 'ADMIN')
LIMIT 1;

-- MEMBER_GRADE 상세
INSERT INTO code_detail (code_group_seq, parent_detail_seq, code_id, code_value, code_name, code_level, sort_order, use_yn, description, status, create_dt, create_id, create_ip, modify_dt, modify_id, modify_ip)
SELECT g.code_group_seq, NULL, 'NORMAL', 'NORMAL', '일반', 1, 10, 'Y', NULL, 'ACTIVE', NOW(), 'SYSTEM', '127.0.0.1', NOW(), 'SYSTEM', '127.0.0.1'
FROM code_group g
WHERE g.code_group_id = 'MEMBER_GRADE'
  AND NOT EXISTS (SELECT 1 FROM code_detail d WHERE d.code_group_seq = g.code_group_seq AND d.code_id = 'NORMAL')
LIMIT 1;

INSERT INTO code_detail (code_group_seq, parent_detail_seq, code_id, code_value, code_name, code_level, sort_order, use_yn, description, status, create_dt, create_id, create_ip, modify_dt, modify_id, modify_ip)
SELECT g.code_group_seq, NULL, 'VIP', 'VIP', 'VIP', 1, 20, 'Y', NULL, 'ACTIVE', NOW(), 'SYSTEM', '127.0.0.1', NOW(), 'SYSTEM', '127.0.0.1'
FROM code_group g
WHERE g.code_group_id = 'MEMBER_GRADE'
  AND NOT EXISTS (SELECT 1 FROM code_detail d WHERE d.code_group_seq = g.code_group_seq AND d.code_id = 'VIP')
LIMIT 1;

-- MEMBER_STATUS 상세
INSERT INTO code_detail (code_group_seq, parent_detail_seq, code_id, code_value, code_name, code_level, sort_order, use_yn, description, status, create_dt, create_id, create_ip, modify_dt, modify_id, modify_ip)
SELECT g.code_group_seq, NULL, 'ACTIVE', 'ACTIVE', '정상', 1, 10, 'Y', NULL, 'ACTIVE', NOW(), 'SYSTEM', '127.0.0.1', NOW(), 'SYSTEM', '127.0.0.1'
FROM code_group g
WHERE g.code_group_id = 'MEMBER_STATUS'
  AND NOT EXISTS (SELECT 1 FROM code_detail d WHERE d.code_group_seq = g.code_group_seq AND d.code_id = 'ACTIVE')
LIMIT 1;

INSERT INTO code_detail (code_group_seq, parent_detail_seq, code_id, code_value, code_name, code_level, sort_order, use_yn, description, status, create_dt, create_id, create_ip, modify_dt, modify_id, modify_ip)
SELECT g.code_group_seq, NULL, 'SUSPENDED', 'SUSPENDED', '정지', 1, 20, 'Y', NULL, 'ACTIVE', NOW(), 'SYSTEM', '127.0.0.1', NOW(), 'SYSTEM', '127.0.0.1'
FROM code_group g
WHERE g.code_group_id = 'MEMBER_STATUS'
  AND NOT EXISTS (SELECT 1 FROM code_detail d WHERE d.code_group_seq = g.code_group_seq AND d.code_id = 'SUSPENDED')
LIMIT 1;

INSERT INTO code_detail (code_group_seq, parent_detail_seq, code_id, code_value, code_name, code_level, sort_order, use_yn, description, status, create_dt, create_id, create_ip, modify_dt, modify_id, modify_ip)
SELECT g.code_group_seq, NULL, 'WITHDRAWN', 'WITHDRAWN', '탈퇴', 1, 30, 'Y', NULL, 'ACTIVE', NOW(), 'SYSTEM', '127.0.0.1', NOW(), 'SYSTEM', '127.0.0.1'
FROM code_group g
WHERE g.code_group_id = 'MEMBER_STATUS'
  AND NOT EXISTS (SELECT 1 FROM code_detail d WHERE d.code_group_seq = g.code_group_seq AND d.code_id = 'WITHDRAWN')
LIMIT 1;

-- ---------------------------------------------------------------------
-- ROLE_MENU: USER(조회만), ADMIN(전체) — permit-all false 시 로그인 검증용
-- ---------------------------------------------------------------------
INSERT IGNORE INTO ROLE_MENU (ROLE_ID, MENU_ID, CAN_READ, CAN_CREATE, CAN_UPDATE, CAN_DELETE, USE_YN, CRT_DT, UPD_DT)
SELECT r.ROLE_ID, m.MENU_ID, 'Y', 'N', 'N', 'N', 'Y', NOW(), NOW()
FROM ROLE r
CROSS JOIN MENU m
WHERE r.ROLE_CODE = 'USER' AND m.USE_YN = 'Y';

INSERT IGNORE INTO ROLE_MENU (ROLE_ID, MENU_ID, CAN_READ, CAN_CREATE, CAN_UPDATE, CAN_DELETE, USE_YN, CRT_DT, UPD_DT)
SELECT r.ROLE_ID, m.MENU_ID, 'Y', 'Y', 'Y', 'Y', 'Y', NOW(), NOW()
FROM ROLE r
CROSS JOIN MENU m
WHERE r.ROLE_CODE = 'ADMIN' AND m.USE_YN = 'Y';
