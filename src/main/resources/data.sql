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
