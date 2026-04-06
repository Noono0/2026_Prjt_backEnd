-- -- 역할-메뉴 매핑 / AuthorityBuilder 와 동일한 MENU_CODE (MenuAuthorities 상수)
-- -- 최초 1회만 삽입 (MENU_CODE 유니크 기준)

SELECT * FROM MENU;

-- INSERT IGNORE INTO MENU (MENU_CODE, MENU_NAME, MENU_PATH, PARENT_MENU_ID, SORT_ORDER, USE_YN, CRT_DT, UPD_DT)
-- VALUES
--     ('MEMBER', '회원관리', '/members', NULL, 10, 'Y', NOW(), NOW()),
--     ('MENU', '메뉴관리', '/menus', NULL, 20, 'Y', NOW(), NOW()),
--     ('ROLE', '권한관리', '/roles', NULL, 30, 'Y', NOW(), NOW()),
--     ('CODE_GROUP', '코드그룹', '/common-codes', NULL, 40, 'Y', NOW(), NOW()),
--     ('CODE_DETAIL', '코드상세', '/common-codes', NULL, 50, 'Y', NOW(), NOW()),
--     ('PRODUCT', '상품(샘플)', '/products', NULL, 60, 'Y', NOW(), NOW()),
--     ('ORDER', '주문(샘플)', '/orders', NULL, 70, 'Y', NOW(), NOW());
--
-- -- 트리 UI 확인용 하위 메뉴 (루트만 있으면 화면이 평면 목록처럼 보임)
-- INSERT IGNORE INTO MENU (MENU_CODE, MENU_NAME, MENU_PATH, PARENT_MENU_ID, SORT_ORDER, USE_YN, CRT_DT, UPD_DT)
-- SELECT 'MEMBER_SUB', '회원 하위(샘플)', '/members', m.MENU_ID, 1, 'Y', NOW(), NOW()
-- FROM MENU m WHERE m.MENU_CODE = 'MEMBER' LIMIT 1;
--
-- INSERT IGNORE INTO MENU (MENU_CODE, MENU_NAME, MENU_PATH, PARENT_MENU_ID, SORT_ORDER, USE_YN, CRT_DT, UPD_DT)
-- SELECT 'MENU_SUB', '메뉴 하위(샘플)', '/menus', m.MENU_ID, 1, 'Y', NOW(), NOW()
-- FROM MENU m WHERE m.MENU_CODE = 'MENU' LIMIT 1;
--
-- INSERT IGNORE INTO MENU (MENU_CODE, MENU_NAME, MENU_PATH, PARENT_MENU_ID, SORT_ORDER, USE_YN, CRT_DT, UPD_DT)
-- SELECT 'MEMBER_SUB2', '회원 2depth(샘플)', '/members', m.MENU_ID, 1, 'Y', NOW(), NOW()
-- FROM MENU m WHERE m.MENU_CODE = 'MEMBER_SUB' LIMIT 1;
--
-- -- ---------------------------------------------------------------------
-- -- ROLE (시스템 권한) + MEMBER_ROLE 공통코드와 동일 코드 체계 유지
-- -- ---------------------------------------------------------------------
-- INSERT IGNORE INTO ROLE (ROLE_CODE, ROLE_NAME, USE_YN, CRT_DT, UPD_DT) VALUES
--     ('USER', '일반회원', 'Y', NOW(), NOW()),
--     ('ADMIN', '관리자', 'Y', NOW(), NOW());
--
-- -- ---------------------------------------------------------------------
-- -- 공통코드: MEMBER_ROLE / MEMBER_GRADE / MEMBER_STATUS
-- -- (code_group / code_detail — JPA 엔티티 테이블명 소문자)
-- -- ---------------------------------------------------------------------
-- INSERT IGNORE INTO code_group (code_group_id, code_group_name, description, sort_order, use_yn, create_dt, create_id, create_ip, modify_dt, modify_id, modify_ip, status)
-- VALUES
--     ('MEMBER_ROLE', '회원 시스템 권한(ROLE)', 'ROLE·MEMBER_ROLE과 동일 코드. Spring Security 메뉴 권한에 사용', 110, 'Y', NOW(), 'SYSTEM', '127.0.0.1', NOW(), 'SYSTEM', '127.0.0.1', 'ACTIVE'),
--     ('MEMBER_GRADE', '회원 등급', '쇼핑몰 VIP 등 비즈니스 등급(MEMBER.GRADE_CODE)', 120, 'Y', NOW(), 'SYSTEM', '127.0.0.1', NOW(), 'SYSTEM', '127.0.0.1', 'ACTIVE'),
--     ('MEMBER_STATUS', '회원 계정 상태', '로그인 가능 여부 등(MEMBER.STATUS_CODE)', 130, 'Y', NOW(), 'SYSTEM', '127.0.0.1', NOW(), 'SYSTEM', '127.0.0.1', 'ACTIVE');
--
-- -- MEMBER_ROLE 상세 (ROLE_CODE와 동일)
-- INSERT INTO code_detail (code_group_seq, parent_detail_seq, code_id, code_value, code_name, code_level, sort_order, use_yn, description, status, create_dt, create_id, create_ip, modify_dt, modify_id, modify_ip)
-- SELECT g.code_group_seq, NULL, 'USER', 'USER', '일반회원', 1, 10, 'Y', NULL, 'ACTIVE', NOW(), 'SYSTEM', '127.0.0.1', NOW(), 'SYSTEM', '127.0.0.1'
-- FROM code_group g
-- WHERE g.code_group_id = 'MEMBER_ROLE'
--   AND NOT EXISTS (SELECT 1 FROM code_detail d WHERE d.code_group_seq = g.code_group_seq AND d.code_id = 'USER')
-- LIMIT 1;
--
-- INSERT INTO code_detail (code_group_seq, parent_detail_seq, code_id, code_value, code_name, code_level, sort_order, use_yn, description, status, create_dt, create_id, create_ip, modify_dt, modify_id, modify_ip)
-- SELECT g.code_group_seq, NULL, 'ADMIN', 'ADMIN', '관리자', 1, 20, 'Y', NULL, 'ACTIVE', NOW(), 'SYSTEM', '127.0.0.1', NOW(), 'SYSTEM', '127.0.0.1'
-- FROM code_group g
-- WHERE g.code_group_id = 'MEMBER_ROLE'
--   AND NOT EXISTS (SELECT 1 FROM code_detail d WHERE d.code_group_seq = g.code_group_seq AND d.code_id = 'ADMIN')
-- LIMIT 1;
--
-- -- MEMBER_GRADE 상세
-- INSERT INTO code_detail (code_group_seq, parent_detail_seq, code_id, code_value, code_name, code_level, sort_order, use_yn, description, status, create_dt, create_id, create_ip, modify_dt, modify_id, modify_ip)
-- SELECT g.code_group_seq, NULL, 'NORMAL', 'NORMAL', '일반', 1, 10, 'Y', NULL, 'ACTIVE', NOW(), 'SYSTEM', '127.0.0.1', NOW(), 'SYSTEM', '127.0.0.1'
-- FROM code_group g
-- WHERE g.code_group_id = 'MEMBER_GRADE'
--   AND NOT EXISTS (SELECT 1 FROM code_detail d WHERE d.code_group_seq = g.code_group_seq AND d.code_id = 'NORMAL')
-- LIMIT 1;
--
-- INSERT INTO code_detail (code_group_seq, parent_detail_seq, code_id, code_value, code_name, code_level, sort_order, use_yn, description, status, create_dt, create_id, create_ip, modify_dt, modify_id, modify_ip)
-- SELECT g.code_group_seq, NULL, 'VIP', 'VIP', 'VIP', 1, 20, 'Y', NULL, 'ACTIVE', NOW(), 'SYSTEM', '127.0.0.1', NOW(), 'SYSTEM', '127.0.0.1'
-- FROM code_group g
-- WHERE g.code_group_id = 'MEMBER_GRADE'
--   AND NOT EXISTS (SELECT 1 FROM code_detail d WHERE d.code_group_seq = g.code_group_seq AND d.code_id = 'VIP')
-- LIMIT 1;
--
-- -- MEMBER_STATUS 상세
-- INSERT INTO code_detail (code_group_seq, parent_detail_seq, code_id, code_value, code_name, code_level, sort_order, use_yn, description, status, create_dt, create_id, create_ip, modify_dt, modify_id, modify_ip)
-- SELECT g.code_group_seq, NULL, 'ACTIVE', 'ACTIVE', '정상', 1, 10, 'Y', NULL, 'ACTIVE', NOW(), 'SYSTEM', '127.0.0.1', NOW(), 'SYSTEM', '127.0.0.1'
-- FROM code_group g
-- WHERE g.code_group_id = 'MEMBER_STATUS'
--   AND NOT EXISTS (SELECT 1 FROM code_detail d WHERE d.code_group_seq = g.code_group_seq AND d.code_id = 'ACTIVE')
-- LIMIT 1;
--
-- INSERT INTO code_detail (code_group_seq, parent_detail_seq, code_id, code_value, code_name, code_level, sort_order, use_yn, description, status, create_dt, create_id, create_ip, modify_dt, modify_id, modify_ip)
-- SELECT g.code_group_seq, NULL, 'SUSPENDED', 'SUSPENDED', '정지', 1, 20, 'Y', NULL, 'ACTIVE', NOW(), 'SYSTEM', '127.0.0.1', NOW(), 'SYSTEM', '127.0.0.1'
-- FROM code_group g
-- WHERE g.code_group_id = 'MEMBER_STATUS'
--   AND NOT EXISTS (SELECT 1 FROM code_detail d WHERE d.code_group_seq = g.code_group_seq AND d.code_id = 'SUSPENDED')
-- LIMIT 1;
--
-- INSERT INTO code_detail (code_group_seq, parent_detail_seq, code_id, code_value, code_name, code_level, sort_order, use_yn, description, status, create_dt, create_id, create_ip, modify_dt, modify_id, modify_ip)
-- SELECT g.code_group_seq, NULL, 'WITHDRAWN', 'WITHDRAWN', '탈퇴', 1, 30, 'Y', NULL, 'ACTIVE', NOW(), 'SYSTEM', '127.0.0.1', NOW(), 'SYSTEM', '127.0.0.1'
-- FROM code_group g
-- WHERE g.code_group_id = 'MEMBER_STATUS'
--   AND NOT EXISTS (SELECT 1 FROM code_detail d WHERE d.code_group_seq = g.code_group_seq AND d.code_id = 'WITHDRAWN')
-- LIMIT 1;
--
-- -- ---------------------------------------------------------------------
-- -- ROLE_MENU: USER(조회만), ADMIN(전체) — permit-all false 시 로그인 검증용
-- -- ---------------------------------------------------------------------
-- INSERT IGNORE INTO ROLE_MENU (ROLE_ID, MENU_ID, CAN_READ, CAN_CREATE, CAN_UPDATE, CAN_DELETE, USE_YN, CRT_DT, UPD_DT)
-- SELECT r.ROLE_ID, m.MENU_ID, 'Y', 'N', 'N', 'N', 'Y', NOW(), NOW()
-- FROM ROLE r
-- CROSS JOIN MENU m
-- WHERE r.ROLE_CODE = 'USER' AND m.USE_YN = 'Y';
--
-- INSERT IGNORE INTO ROLE_MENU (ROLE_ID, MENU_ID, CAN_READ, CAN_CREATE, CAN_UPDATE, CAN_DELETE, USE_YN, CRT_DT, UPD_DT)
-- SELECT r.ROLE_ID, m.MENU_ID, 'Y', 'Y', 'Y', 'Y', 'Y', NOW(), NOW()
-- FROM ROLE r
-- CROSS JOIN MENU m
-- WHERE r.ROLE_CODE = 'ADMIN' AND m.USE_YN = 'Y';
--
-- -- ---------------------------------------------------------------------
-- -- BOARD / NOTICE_BOARD 반응, 신고 기능 보강 컬럼
-- -- ---------------------------------------------------------------------
-- ALTER TABLE board
--     ADD COLUMN IF NOT EXISTS comment_like_count BIGINT DEFAULT 0,
--     ADD COLUMN IF NOT EXISTS comment_report_count BIGINT DEFAULT 0;
--
-- ALTER TABLE notice_board
--     ADD COLUMN IF NOT EXISTS comment_like_count BIGINT DEFAULT 0,
--     ADD COLUMN IF NOT EXISTS comment_report_count BIGINT DEFAULT 0;
--
-- -- 게시글/댓글 반응 중복 방지 로그
-- CREATE TABLE IF NOT EXISTS board_action_log (
--     board_action_log_seq BIGINT AUTO_INCREMENT PRIMARY KEY,
--     board_kind VARCHAR(30) NOT NULL,   -- BOARD, NOTICE_BOARD
--     target_kind VARCHAR(20) NOT NULL,  -- POST, COMMENT
--     target_seq BIGINT NOT NULL,
--     action_type VARCHAR(20) NOT NULL,  -- LIKE, DISLIKE, REPORT
--     member_seq BIGINT NULL,
--     member_id VARCHAR(100) NULL,
--     client_ip VARCHAR(45) NULL,
--     create_dt DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
--     UNIQUE KEY uq_board_action (
--         board_kind, target_kind, target_seq, action_type,
--         member_seq, member_id
--     )
-- );

-- 이미지/에디터 본문 길이 이슈 대응
-- ALTER TABLE member MODIFY COLUMN profile_image_url LONGTEXT NULL;
-- ALTER TABLE board MODIFY COLUMN content LONGTEXT NULL;
-- ALTER TABLE notice_board MODIFY COLUMN content LONGTEXT NULL;

-- -- 자유게시판/공지 컬럼 추가: data.sql이 부팅마다 실행되므로 중복 컬럼 오류 방지 (MySQL)
-- SET @prjt_schema := DATABASE();

-- SET @sql_board_c := (SELECT IF(
--     (SELECT COUNT(*) FROM information_schema.COLUMNS
--      WHERE TABLE_SCHEMA = @prjt_schema AND TABLE_NAME = 'board' AND COLUMN_NAME = 'comment_allowed_yn') = 0,
--     'ALTER TABLE board ADD COLUMN comment_allowed_yn CHAR(1) NOT NULL DEFAULT ''Y''',
--     'SELECT 1'));
-- PREPARE _prjt_stmt FROM @sql_board_c;
-- EXECUTE _prjt_stmt;
-- DEALLOCATE PREPARE _prjt_stmt;

-- SET @sql_board_r := (SELECT IF(
--     (SELECT COUNT(*) FROM information_schema.COLUMNS
--      WHERE TABLE_SCHEMA = @prjt_schema AND TABLE_NAME = 'board' AND COLUMN_NAME = 'reply_allowed_yn') = 0,
--     'ALTER TABLE board ADD COLUMN reply_allowed_yn CHAR(1) NOT NULL DEFAULT ''Y''',
--     'SELECT 1'));
-- PREPARE _prjt_stmt FROM @sql_board_r;
-- EXECUTE _prjt_stmt;
-- DEALLOCATE PREPARE _prjt_stmt;

-- SET @sql_notice_c := (SELECT IF(
--     (SELECT COUNT(*) FROM information_schema.COLUMNS
--      WHERE TABLE_SCHEMA = @prjt_schema AND TABLE_NAME = 'notice_board' AND COLUMN_NAME = 'comment_allowed_yn') = 0,
--     'ALTER TABLE notice_board ADD COLUMN comment_allowed_yn CHAR(1) NOT NULL DEFAULT ''Y''',
--     'SELECT 1'));
-- PREPARE _prjt_stmt FROM @sql_notice_c;
-- EXECUTE _prjt_stmt;
-- DEALLOCATE PREPARE _prjt_stmt;

-- SET @sql_notice_r := (SELECT IF(
--     (SELECT COUNT(*) FROM information_schema.COLUMNS
--      WHERE TABLE_SCHEMA = @prjt_schema AND TABLE_NAME = 'notice_board' AND COLUMN_NAME = 'reply_allowed_yn') = 0,
--     'ALTER TABLE notice_board ADD COLUMN reply_allowed_yn CHAR(1) NOT NULL DEFAULT ''Y''',
--     'SELECT 1'));
-- PREPARE _prjt_stmt FROM @sql_notice_r;
-- EXECUTE _prjt_stmt;
-- DEALLOCATE PREPARE _prjt_stmt;

-- SET @sql_notice_p := (SELECT IF(
--     (SELECT COUNT(*) FROM information_schema.COLUMNS
--      WHERE TABLE_SCHEMA = @prjt_schema AND TABLE_NAME = 'notice_board' AND COLUMN_NAME = 'pin_on_free_board_yn') = 0,
--     'ALTER TABLE notice_board ADD COLUMN pin_on_free_board_yn CHAR(1) NOT NULL DEFAULT ''N''',
--     'SELECT 1'));
-- PREPARE _prjt_stmt FROM @sql_notice_p;
-- EXECUTE _prjt_stmt;
-- DEALLOCATE PREPARE _prjt_stmt;

-- -- 회원별 커스텀 이모티콘(최대 3개는 애플리케이션에서 검증)
-- CREATE TABLE IF NOT EXISTS member_emoticon (
--     member_emoticon_seq BIGINT AUTO_INCREMENT PRIMARY KEY,
--     member_seq          BIGINT      NOT NULL,
--     image_url           VARCHAR(512) NOT NULL,
--     sort_order          INT         NOT NULL DEFAULT 0,
--     create_dt           DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
--     KEY idx_member_emoticon_member (member_seq),
--     CONSTRAINT fk_member_emoticon_member FOREIGN KEY (member_seq) REFERENCES member (member_seq) ON DELETE CASCADE
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -- 자유게시판 댓글(대댓글 1단계: parent 가 루트만 허용)
-- CREATE TABLE IF NOT EXISTS board_comment (
--     board_comment_seq        BIGINT AUTO_INCREMENT PRIMARY KEY,
--     board_seq                BIGINT       NOT NULL,
--     parent_board_comment_seq BIGINT       NULL,
--     writer_member_seq        BIGINT       NULL,
--     writer_name              VARCHAR(100) NULL,
--     content                  LONGTEXT     NULL,
--     emoticon_seq_1           BIGINT       NULL,
--     emoticon_seq_2           BIGINT       NULL,
--     emoticon_seq_3           BIGINT       NULL,
--     like_count               BIGINT       NOT NULL DEFAULT 0,
--     dislike_count            BIGINT       NOT NULL DEFAULT 0,
--     report_count             BIGINT       NOT NULL DEFAULT 0,
--     show_yn                  CHAR(1)      NOT NULL DEFAULT 'Y',
--     create_dt                DATETIME     NULL,
--     create_id                VARCHAR(50)  NULL,
--     create_ip                VARCHAR(45)  NULL,
--     modify_dt                DATETIME     NULL,
--     KEY idx_board_comment_board (board_seq),
--     KEY idx_board_comment_parent (parent_board_comment_seq),
--     CONSTRAINT fk_board_comment_board FOREIGN KEY (board_seq) REFERENCES board (board_seq) ON DELETE CASCADE,
--     CONSTRAINT fk_board_comment_parent FOREIGN KEY (parent_board_comment_seq) REFERENCES board_comment (board_comment_seq) ON DELETE CASCADE,
--     CONSTRAINT fk_board_comment_e1 FOREIGN KEY (emoticon_seq_1) REFERENCES member_emoticon (member_emoticon_seq) ON DELETE SET NULL,
--     CONSTRAINT fk_board_comment_e2 FOREIGN KEY (emoticon_seq_2) REFERENCES member_emoticon (member_emoticon_seq) ON DELETE SET NULL,
--     CONSTRAINT fk_board_comment_e3 FOREIGN KEY (emoticon_seq_3) REFERENCES member_emoticon (member_emoticon_seq) ON DELETE SET NULL
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- CREATE TABLE IF NOT EXISTS board_comment_vote (
--     board_comment_seq BIGINT   NOT NULL,
--     member_seq        BIGINT   NOT NULL,
--     vote_type         CHAR(1)  NOT NULL COMMENT 'L=like,D=dislike',
--     create_dt         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
--     PRIMARY KEY (board_comment_seq, member_seq),
--     CONSTRAINT fk_bcv_comment FOREIGN KEY (board_comment_seq) REFERENCES board_comment (board_comment_seq) ON DELETE CASCADE
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- CREATE TABLE IF NOT EXISTS board_comment_report (
--     board_comment_seq BIGINT   NOT NULL,
--     member_seq        BIGINT   NOT NULL,
--     create_dt         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
--     PRIMARY KEY (board_comment_seq, member_seq),
--     CONSTRAINT fk_bcr_comment FOREIGN KEY (board_comment_seq) REFERENCES board_comment (board_comment_seq) ON DELETE CASCADE
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- CREATE TABLE IF NOT EXISTS notice_board_comment (
--     notice_board_comment_seq        BIGINT AUTO_INCREMENT PRIMARY KEY,
--     notice_board_seq                BIGINT       NOT NULL,
--     parent_notice_board_comment_seq BIGINT       NULL,
--     writer_member_seq               BIGINT       NULL,
--     writer_name                     VARCHAR(100) NULL,
--     content                         LONGTEXT     NULL,
--     emoticon_seq_1                  BIGINT       NULL,
--     emoticon_seq_2                  BIGINT       NULL,
--     emoticon_seq_3                  BIGINT       NULL,
--     like_count                      BIGINT       NOT NULL DEFAULT 0,
--     dislike_count                   BIGINT       NOT NULL DEFAULT 0,
--     report_count                    BIGINT       NOT NULL DEFAULT 0,
--     show_yn                         CHAR(1)      NOT NULL DEFAULT 'Y',
--     create_dt                       DATETIME     NULL,
--     create_id                       VARCHAR(50)  NULL,
--     create_ip                       VARCHAR(45)  NULL,
--     modify_dt                       DATETIME     NULL,
--     KEY idx_notice_board_comment_notice (notice_board_seq),
--     KEY idx_notice_board_comment_parent (parent_notice_board_comment_seq),
--     CONSTRAINT fk_nbc_notice FOREIGN KEY (notice_board_seq) REFERENCES notice_board (notice_board_seq) ON DELETE CASCADE,
--     CONSTRAINT fk_nbc_parent FOREIGN KEY (parent_notice_board_comment_seq) REFERENCES notice_board_comment (notice_board_comment_seq) ON DELETE CASCADE,
--     CONSTRAINT fk_nbc_e1 FOREIGN KEY (emoticon_seq_1) REFERENCES member_emoticon (member_emoticon_seq) ON DELETE SET NULL,
--     CONSTRAINT fk_nbc_e2 FOREIGN KEY (emoticon_seq_2) REFERENCES member_emoticon (member_emoticon_seq) ON DELETE SET NULL,
--     CONSTRAINT fk_nbc_e3 FOREIGN KEY (emoticon_seq_3) REFERENCES member_emoticon (member_emoticon_seq) ON DELETE SET NULL
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- CREATE TABLE IF NOT EXISTS notice_board_comment_vote (
--     notice_board_comment_seq BIGINT   NOT NULL,
--     member_seq               BIGINT   NOT NULL,
--     vote_type                CHAR(1)  NOT NULL COMMENT 'L=like,D=dislike',
--     create_dt                DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
--     PRIMARY KEY (notice_board_comment_seq, member_seq),
--     CONSTRAINT fk_nbcv_comment FOREIGN KEY (notice_board_comment_seq) REFERENCES notice_board_comment (notice_board_comment_seq) ON DELETE CASCADE
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- CREATE TABLE IF NOT EXISTS notice_board_comment_report (
--     notice_board_comment_seq BIGINT   NOT NULL,
--     member_seq               BIGINT   NOT NULL,
--     create_dt                DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
--     PRIMARY KEY (notice_board_comment_seq, member_seq),
--     CONSTRAINT fk_nbcr_comment FOREIGN KEY (notice_board_comment_seq) REFERENCES notice_board_comment (notice_board_comment_seq) ON DELETE CASCADE
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 공지사항: 사이트 로드 팝업 설정 (중복 컬럼 방지)
SET @prjt_schema := DATABASE();

SET @sql_nb_pop1 := (SELECT IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @prjt_schema AND TABLE_NAME = 'notice_board' AND COLUMN_NAME = 'popup_yn') = 0,
    'ALTER TABLE notice_board ADD COLUMN popup_yn CHAR(1) NOT NULL DEFAULT ''N''',
    'SELECT 1'));
PREPARE _prjt_stmt FROM @sql_nb_pop1;
EXECUTE _prjt_stmt;
DEALLOCATE PREPARE _prjt_stmt;

SET @sql_nb_pop2 := (SELECT IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @prjt_schema AND TABLE_NAME = 'notice_board' AND COLUMN_NAME = 'popup_type') = 0,
    'ALTER TABLE notice_board ADD COLUMN popup_type VARCHAR(20) NOT NULL DEFAULT ''MODAL''',
    'SELECT 1'));
PREPARE _prjt_stmt FROM @sql_nb_pop2;
EXECUTE _prjt_stmt;
DEALLOCATE PREPARE _prjt_stmt;

SET @sql_nb_pop3 := (SELECT IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @prjt_schema AND TABLE_NAME = 'notice_board' AND COLUMN_NAME = 'popup_width') = 0,
    'ALTER TABLE notice_board ADD COLUMN popup_width INT NOT NULL DEFAULT 600',
    'SELECT 1'));
PREPARE _prjt_stmt FROM @sql_nb_pop3;
EXECUTE _prjt_stmt;
DEALLOCATE PREPARE _prjt_stmt;

SET @sql_nb_pop4 := (SELECT IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @prjt_schema AND TABLE_NAME = 'notice_board' AND COLUMN_NAME = 'popup_height') = 0,
    'ALTER TABLE notice_board ADD COLUMN popup_height INT NOT NULL DEFAULT 600',
    'SELECT 1'));
PREPARE _prjt_stmt FROM @sql_nb_pop4;
EXECUTE _prjt_stmt;
DEALLOCATE PREPARE _prjt_stmt;

SET @sql_nb_pop5 := (SELECT IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @prjt_schema AND TABLE_NAME = 'notice_board' AND COLUMN_NAME = 'popup_pos_x') = 0,
    'ALTER TABLE notice_board ADD COLUMN popup_pos_x INT NULL COMMENT ''NULL 이면 가로 중앙''',
    'SELECT 1'));
PREPARE _prjt_stmt FROM @sql_nb_pop5;
EXECUTE _prjt_stmt;
DEALLOCATE PREPARE _prjt_stmt;

SET @sql_nb_pop6 := (SELECT IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @prjt_schema AND TABLE_NAME = 'notice_board' AND COLUMN_NAME = 'popup_pos_y') = 0,
    'ALTER TABLE notice_board ADD COLUMN popup_pos_y INT NULL COMMENT ''NULL 이면 세로 중앙''',
    'SELECT 1'));
PREPARE _prjt_stmt FROM @sql_nb_pop6;
EXECUTE _prjt_stmt;
DEALLOCATE PREPARE _prjt_stmt;

SET @sql_nb_pop_s := (SELECT IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @prjt_schema AND TABLE_NAME = 'notice_board' AND COLUMN_NAME = 'popup_start_dt') = 0,
    'ALTER TABLE notice_board ADD COLUMN popup_start_dt DATETIME NULL COMMENT ''NULL이면 즉시''',
    'SELECT 1'));
PREPARE _prjt_stmt FROM @sql_nb_pop_s;
EXECUTE _prjt_stmt;
DEALLOCATE PREPARE _prjt_stmt;

SET @sql_nb_pop_e := (SELECT IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @prjt_schema AND TABLE_NAME = 'notice_board' AND COLUMN_NAME = 'popup_end_dt') = 0,
    'ALTER TABLE notice_board ADD COLUMN popup_end_dt DATETIME NULL COMMENT ''NULL이면 무기한''',
    'SELECT 1'));
PREPARE _prjt_stmt FROM @sql_nb_pop_e;
EXECUTE _prjt_stmt;
DEALLOCATE PREPARE _prjt_stmt;

-- 사이트 팝업(공지와 분리)
CREATE TABLE IF NOT EXISTS site_popup (
    site_popup_seq BIGINT AUTO_INCREMENT PRIMARY KEY,
    title            VARCHAR(500) NOT NULL,
    content          LONGTEXT     NULL,
    show_yn          CHAR(1)      NOT NULL DEFAULT 'Y',
    use_yn           CHAR(1)      NOT NULL DEFAULT 'Y' COMMENT '행 삭제 여부 소프트삭제',
    popup_type       VARCHAR(20)  NOT NULL DEFAULT 'MODAL' COMMENT 'MODAL | WINDOW',
    popup_width      INT          NOT NULL DEFAULT 600,
    popup_height     INT          NOT NULL DEFAULT 600,
    popup_pos_x      INT          NULL COMMENT 'NULL 이면 가로 중앙',
    popup_pos_y      INT          NULL COMMENT 'NULL 이면 세로 중앙',
    popup_start_dt   DATETIME     NULL COMMENT 'NULL이면 즉시',
    popup_end_dt     DATETIME     NULL COMMENT 'NULL이면 무기한',
    sort_order       INT          NOT NULL DEFAULT 0,
    create_dt        DATETIME     NULL,
    create_id        VARCHAR(50)  NULL,
    create_ip        VARCHAR(45)  NULL,
    modify_dt        DATETIME     NULL,
    modify_id        VARCHAR(50)  NULL,
    modify_ip        VARCHAR(45)  NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 기존 DB: site_popup/board/notice_board/member_emoticon/member 에 use_yn 추가 (이관 INSERT보다 먼저)
SET @prjt_schema := DATABASE();

SET @sql_board_use := (SELECT IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @prjt_schema AND TABLE_NAME = 'board' AND COLUMN_NAME = 'use_yn') = 0,
    'ALTER TABLE board ADD COLUMN use_yn CHAR(1) NOT NULL DEFAULT ''Y'' COMMENT ''행 삭제 여부''',
    'SELECT 1'));
PREPARE _prjt_stmt FROM @sql_board_use;
EXECUTE _prjt_stmt;
DEALLOCATE PREPARE _prjt_stmt;

SET @sql_nb_use := (SELECT IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @prjt_schema AND TABLE_NAME = 'notice_board' AND COLUMN_NAME = 'use_yn') = 0,
    'ALTER TABLE notice_board ADD COLUMN use_yn CHAR(1) NOT NULL DEFAULT ''Y'' COMMENT ''행 삭제 여부''',
    'SELECT 1'));
PREPARE _prjt_stmt FROM @sql_nb_use;
EXECUTE _prjt_stmt;
DEALLOCATE PREPARE _prjt_stmt;

SET @sql_sp_use := (SELECT IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @prjt_schema AND TABLE_NAME = 'site_popup' AND COLUMN_NAME = 'use_yn') = 0,
    'ALTER TABLE site_popup ADD COLUMN use_yn CHAR(1) NOT NULL DEFAULT ''Y'' COMMENT ''행 삭제 여부''',
    'SELECT 1'));
PREPARE _prjt_stmt FROM @sql_sp_use;
EXECUTE _prjt_stmt;
DEALLOCATE PREPARE _prjt_stmt;

SET @sql_me_use := (SELECT IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @prjt_schema AND TABLE_NAME = 'member_emoticon' AND COLUMN_NAME = 'use_yn') = 0,
    'ALTER TABLE member_emoticon ADD COLUMN use_yn CHAR(1) NOT NULL DEFAULT ''Y'' COMMENT ''행 삭제 여부''',
    'SELECT 1'));
PREPARE _prjt_stmt FROM @sql_me_use;
EXECUTE _prjt_stmt;
DEALLOCATE PREPARE _prjt_stmt;

SET @sql_mem_use := (SELECT IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @prjt_schema AND TABLE_NAME = 'member' AND COLUMN_NAME = 'use_yn') = 0,
    'ALTER TABLE member ADD COLUMN use_yn CHAR(1) NOT NULL DEFAULT ''Y'' COMMENT ''계정 사용 여부''',
    'SELECT 1'));
PREPARE _prjt_stmt FROM @sql_mem_use;
EXECUTE _prjt_stmt;
DEALLOCATE PREPARE _prjt_stmt;

-- notice_board에 팝업으로 쓰이던 글을 한 번만 이관(테이블이 비어 있을 때만)
INSERT INTO site_popup (
    title, content, show_yn, use_yn, popup_type, popup_width, popup_height, popup_pos_x, popup_pos_y,
    popup_start_dt, popup_end_dt, sort_order, create_dt, create_id, create_ip, modify_dt, modify_id, modify_ip
)
SELECT b.title, b.content, b.show_yn, 'Y', b.popup_type, b.popup_width, b.popup_height, b.popup_pos_x, b.popup_pos_y,
    b.popup_start_dt, b.popup_end_dt, 0, b.create_dt, b.create_id, b.create_ip, b.modify_dt, b.modify_id, b.modify_ip
FROM notice_board b
WHERE b.popup_yn = 'Y'
  AND (SELECT COUNT(1) FROM site_popup) = 0;

INSERT INTO MENU (MENU_CODE, MENU_NAME, MENU_PATH, PARENT_MENU_ID, SORT_ORDER, USE_YN, CRT_DT, UPD_DT)
SELECT 'SITE_POPUP', '팝업관리', '/site-popups', NULL, 25, 'Y', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM MENU WHERE MENU_CODE = 'SITE_POPUP' LIMIT 1);

INSERT INTO ROLE_MENU (ROLE_ID, MENU_ID, CAN_READ, CAN_CREATE, CAN_UPDATE, CAN_DELETE, USE_YN, CRT_DT, UPD_DT)
SELECT r.ROLE_ID, m.MENU_ID, 'Y', 'Y', 'Y', 'Y', 'Y', NOW(), NOW()
FROM ROLE r
CROSS JOIN MENU m
WHERE r.ROLE_CODE = 'ADMIN'
  AND m.MENU_CODE = 'SITE_POPUP'
  AND NOT EXISTS (
    SELECT 1 FROM ROLE_MENU rm
    WHERE rm.ROLE_ID = r.ROLE_ID AND rm.MENU_ID = m.MENU_ID
  );

-- 비속어·광고 필터 단어 (게시글·댓글·팝업 저장 시 치환)
CREATE TABLE IF NOT EXISTS content_filter_word (
    content_filter_word_seq BIGINT AUTO_INCREMENT PRIMARY KEY,
    category     VARCHAR(20)  NOT NULL COMMENT 'PROFANITY | AD',
    keyword      VARCHAR(200) NOT NULL,
    use_yn       CHAR(1)      NOT NULL DEFAULT 'Y',
    sort_order   INT          NOT NULL DEFAULT 0,
    remark       VARCHAR(500) NULL,
    create_dt    DATETIME     NULL,
    modify_dt    DATETIME     NULL,
    KEY idx_cfw_category (category),
    KEY idx_cfw_use (use_yn)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO MENU (MENU_CODE, MENU_NAME, MENU_PATH, PARENT_MENU_ID, SORT_ORDER, USE_YN, CRT_DT, UPD_DT)
SELECT 'CONTENT_FILTER', '비속어·광고 필터', '/content-filter', NULL, 26, 'Y', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM MENU WHERE MENU_CODE = 'CONTENT_FILTER' LIMIT 1);

INSERT INTO ROLE_MENU (ROLE_ID, MENU_ID, CAN_READ, CAN_CREATE, CAN_UPDATE, CAN_DELETE, USE_YN, CRT_DT, UPD_DT)
SELECT r.ROLE_ID, m.MENU_ID, 'Y', 'Y', 'Y', 'Y', 'Y', NOW(), NOW()
FROM ROLE r
CROSS JOIN MENU m
WHERE r.ROLE_CODE = 'ADMIN'
  AND m.MENU_CODE = 'CONTENT_FILTER'
  AND NOT EXISTS (
    SELECT 1 FROM ROLE_MENU rm
    WHERE rm.ROLE_ID = r.ROLE_ID AND rm.MENU_ID = m.MENU_ID
  );

-- 일정·생일 달력 (자유게시판과 별도)
CREATE TABLE IF NOT EXISTS calendar_schedule (
    calendar_schedule_seq BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_kind          VARCHAR(20)  NOT NULL COMMENT 'GENERAL | BIRTHDAY',
    title               VARCHAR(500) NOT NULL,
    category_code       VARCHAR(100) NULL COMMENT '공통코드 그룹 A0003 code_value',
    event_color         VARCHAR(20)  NULL COMMENT '달력 표시용 #RGB 또는 #RRGGBB',
    content             LONGTEXT     NULL,
    start_date          DATE         NULL COMMENT '일정 시작일',
    end_date            DATE         NULL COMMENT '일정 종료일(포함)',
    start_time          TIME         NULL COMMENT '시작 시각(선택, 일정만)',
    end_time            TIME         NULL COMMENT '종료 시각(선택, 일정만)',
    birth_month         TINYINT      NULL COMMENT '생일 월 1-12',
    birth_day           TINYINT      NULL COMMENT '생일 일 1-31',
    use_yn              CHAR(1)      NOT NULL DEFAULT 'Y',
    create_id           VARCHAR(50)  NULL,
    create_ip           VARCHAR(45)  NULL,
    create_dt           DATETIME     NULL,
    modify_id           VARCHAR(50)  NULL,
    modify_ip           VARCHAR(45)  NULL,
    modify_dt           DATETIME     NULL,
    KEY idx_cs_kind (event_kind),
    KEY idx_cs_range (start_date, end_date),
    KEY idx_cs_use (use_yn)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 이미 calendar_schedule이 있고 위 컬럼이 없을 때만 수동 실행 (중복 시 오류)
-- ALTER TABLE calendar_schedule ADD COLUMN start_time TIME NULL COMMENT '시작 시각(선택)' AFTER end_date;
-- ALTER TABLE calendar_schedule ADD COLUMN end_time TIME NULL COMMENT '종료 시각(선택)' AFTER start_time;
-- ALTER TABLE calendar_schedule ADD COLUMN category_code VARCHAR(100) NULL COMMENT '공통코드 A0003' AFTER title;

INSERT INTO MENU (MENU_CODE, MENU_NAME, MENU_PATH, PARENT_MENU_ID, SORT_ORDER, USE_YN, CRT_DT, UPD_DT)
SELECT 'CALENDAR_SCHEDULE', '일정 달력', '/calendar-schedules', NULL, 27, 'Y', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM MENU WHERE MENU_CODE = 'CALENDAR_SCHEDULE' LIMIT 1);

INSERT INTO ROLE_MENU (ROLE_ID, MENU_ID, CAN_READ, CAN_CREATE, CAN_UPDATE, CAN_DELETE, USE_YN, CRT_DT, UPD_DT)
SELECT r.ROLE_ID, m.MENU_ID, 'Y', 'Y', 'Y', 'Y', 'Y', NOW(), NOW()
FROM ROLE r
CROSS JOIN MENU m
WHERE r.ROLE_CODE = 'ADMIN'
  AND m.MENU_CODE = 'CALENDAR_SCHEDULE'
  AND NOT EXISTS (
    SELECT 1 FROM ROLE_MENU rm
    WHERE rm.ROLE_ID = r.ROLE_ID AND rm.MENU_ID = m.MENU_ID
  );

INSERT INTO MENU (MENU_CODE, MENU_NAME, MENU_PATH, PARENT_MENU_ID, SORT_ORDER, USE_YN, CRT_DT, UPD_DT)
SELECT 'POINT_RANKING', '포인트 랭킹', '/point-ranking', NULL, 28, 'Y', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM MENU WHERE MENU_CODE = 'POINT_RANKING' LIMIT 1);

INSERT INTO ROLE_MENU (ROLE_ID, MENU_ID, CAN_READ, CAN_CREATE, CAN_UPDATE, CAN_DELETE, USE_YN, CRT_DT, UPD_DT)
SELECT r.ROLE_ID, m.MENU_ID, 'Y', 'Y', 'Y', 'Y', 'Y', NOW(), NOW()
FROM ROLE r
CROSS JOIN MENU m
WHERE r.ROLE_CODE = 'ADMIN'
  AND m.MENU_CODE = 'POINT_RANKING'
  AND NOT EXISTS (
    SELECT 1 FROM ROLE_MENU rm
    WHERE rm.ROLE_ID = r.ROLE_ID AND rm.MENU_ID = m.MENU_ID
  );

INSERT INTO MENU (MENU_CODE, MENU_NAME, MENU_PATH, PARENT_MENU_ID, SORT_ORDER, USE_YN, CRT_DT, UPD_DT)
SELECT 'POINT_POLICY', '포인트 정책', '/point-policy', NULL, 29, 'Y', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM MENU WHERE MENU_CODE = 'POINT_POLICY' LIMIT 1);

INSERT INTO ROLE_MENU (ROLE_ID, MENU_ID, CAN_READ, CAN_CREATE, CAN_UPDATE, CAN_DELETE, USE_YN, CRT_DT, UPD_DT)
SELECT r.ROLE_ID, m.MENU_ID, 'Y', 'Y', 'Y', 'Y', 'Y', NOW(), NOW()
FROM ROLE r
CROSS JOIN MENU m
WHERE r.ROLE_CODE = 'ADMIN'
  AND m.MENU_CODE = 'POINT_POLICY'
  AND NOT EXISTS (
    SELECT 1 FROM ROLE_MENU rm
    WHERE rm.ROLE_ID = r.ROLE_ID AND rm.MENU_ID = m.MENU_ID
  );

-- 이벤트 대결: 주제 2~5개(event_battle_option), 유저당 이벤트당 1회 베팅(UNIQUE)
CREATE TABLE IF NOT EXISTS event_battle (
    event_battle_seq       BIGINT AUTO_INCREMENT PRIMARY KEY,
    title                  VARCHAR(500) NOT NULL,
    status                 VARCHAR(20)  NOT NULL DEFAULT 'OPEN' COMMENT 'OPEN | SETTLED | CANCELLED',
    vote_limit_per_member  INT          NOT NULL DEFAULT 1 COMMENT '1인당 선택 가능한 주제 수',
    vote_only_yn           CHAR(1)      NOT NULL DEFAULT 'N' COMMENT 'Y=투표 전용(베팅 불가)',
    winner_option_seq      BIGINT       NULL COMMENT 'event_battle_option.event_battle_option_seq',
    creator_member_seq     BIGINT       NOT NULL,
    use_yn                 CHAR(1)      NOT NULL DEFAULT 'Y',
    create_id              VARCHAR(50)  NULL,
    create_ip              VARCHAR(45)  NULL,
    create_dt              DATETIME     NULL,
    modify_id              VARCHAR(50)  NULL,
    modify_ip              VARCHAR(45)  NULL,
    modify_dt              DATETIME     NULL,
    settle_dt              DATETIME     NULL,
    KEY idx_eb_status (status),
    KEY idx_eb_creator (creator_member_seq),
    CONSTRAINT fk_eb_creator FOREIGN KEY (creator_member_seq) REFERENCES member (member_seq)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 기존 DB에 event_battle 테이블만 있고 컬럼이 없는 경우: CREATE IF NOT EXISTS는 테이블을 갱신하지 않음
SET @prjt_schema := DATABASE();
SET @sql_eb_winner := (SELECT IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @prjt_schema AND TABLE_NAME = 'event_battle' AND COLUMN_NAME = 'winner_option_seq') = 0,
    'ALTER TABLE event_battle ADD COLUMN winner_option_seq BIGINT NULL COMMENT ''event_battle_option.event_battle_option_seq''',
    'SELECT 1'));
PREPARE _prjt_stmt FROM @sql_eb_winner;
EXECUTE _prjt_stmt;
DEALLOCATE PREPARE _prjt_stmt;
SET @sql_eb_vote_limit := (SELECT IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @prjt_schema AND TABLE_NAME = 'event_battle' AND COLUMN_NAME = 'vote_limit_per_member') = 0,
    'ALTER TABLE event_battle ADD COLUMN vote_limit_per_member INT NOT NULL DEFAULT 1 COMMENT ''1인당 선택 가능한 주제 수''',
    'SELECT 1'));
PREPARE _prjt_stmt FROM @sql_eb_vote_limit;
EXECUTE _prjt_stmt;
DEALLOCATE PREPARE _prjt_stmt;

SET @sql_eb_vote_only := (SELECT IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @prjt_schema AND TABLE_NAME = 'event_battle' AND COLUMN_NAME = 'vote_only_yn') = 0,
    'ALTER TABLE event_battle ADD COLUMN vote_only_yn CHAR(1) NOT NULL DEFAULT ''N'' COMMENT ''Y=투표 전용(베팅 불가)''',
    'SELECT 1'));
PREPARE _prjt_stmt FROM @sql_eb_vote_only;
EXECUTE _prjt_stmt;
DEALLOCATE PREPARE _prjt_stmt;

-- 구 좌/우 컬럼이 NOT NULL 이면 신규 INSERT 가 실패 → NULL 허용(완전 제거는 db/manual/migrate-event-battle-existing-db.sql 참고)
SET @sql_eb_legacy := (SELECT IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @prjt_schema AND TABLE_NAME = 'event_battle' AND COLUMN_NAME = 'left_label') > 0,
    'ALTER TABLE event_battle MODIFY COLUMN left_label VARCHAR(500) NULL DEFAULT NULL', 'SELECT 1'));
PREPARE _prjt_stmt FROM @sql_eb_legacy;
EXECUTE _prjt_stmt;
DEALLOCATE PREPARE _prjt_stmt;
SET @sql_eb_legacy := (SELECT IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @prjt_schema AND TABLE_NAME = 'event_battle' AND COLUMN_NAME = 'right_label') > 0,
    'ALTER TABLE event_battle MODIFY COLUMN right_label VARCHAR(500) NULL DEFAULT NULL', 'SELECT 1'));
PREPARE _prjt_stmt FROM @sql_eb_legacy;
EXECUTE _prjt_stmt;
DEALLOCATE PREPARE _prjt_stmt;
SET @sql_eb_legacy := (SELECT IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @prjt_schema AND TABLE_NAME = 'event_battle' AND COLUMN_NAME = 'left_points_total') > 0,
    'ALTER TABLE event_battle MODIFY COLUMN left_points_total BIGINT NULL DEFAULT 0', 'SELECT 1'));
PREPARE _prjt_stmt FROM @sql_eb_legacy;
EXECUTE _prjt_stmt;
DEALLOCATE PREPARE _prjt_stmt;
SET @sql_eb_legacy := (SELECT IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @prjt_schema AND TABLE_NAME = 'event_battle' AND COLUMN_NAME = 'right_points_total') > 0,
    'ALTER TABLE event_battle MODIFY COLUMN right_points_total BIGINT NULL DEFAULT 0', 'SELECT 1'));
PREPARE _prjt_stmt FROM @sql_eb_legacy;
EXECUTE _prjt_stmt;
DEALLOCATE PREPARE _prjt_stmt;
SET @sql_eb_legacy := (SELECT IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @prjt_schema AND TABLE_NAME = 'event_battle' AND COLUMN_NAME = 'winner_side') > 0,
    'ALTER TABLE event_battle MODIFY COLUMN winner_side VARCHAR(20) NULL DEFAULT NULL', 'SELECT 1'));
PREPARE _prjt_stmt FROM @sql_eb_legacy;
EXECUTE _prjt_stmt;
DEALLOCATE PREPARE _prjt_stmt;

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

CREATE TABLE IF NOT EXISTS event_battle_bet (
    event_battle_bet_seq     BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_battle_seq         BIGINT NOT NULL,
    event_battle_option_seq  BIGINT NOT NULL,
    member_seq               BIGINT NOT NULL,
    point_amount             BIGINT NOT NULL,
    create_dt                DATETIME NULL,
    KEY idx_ebb_event_member (event_battle_seq, member_seq),
    KEY idx_ebb_event (event_battle_seq),
    KEY idx_ebb_event_seq (event_battle_seq, event_battle_bet_seq),
    CONSTRAINT fk_ebb_event FOREIGN KEY (event_battle_seq) REFERENCES event_battle (event_battle_seq) ON DELETE CASCADE,
    CONSTRAINT fk_ebb_option FOREIGN KEY (event_battle_option_seq) REFERENCES event_battle_option (event_battle_option_seq),
    CONSTRAINT fk_ebb_member FOREIGN KEY (member_seq) REFERENCES member (member_seq)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS event_battle_vote (
    event_battle_vote_seq    BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_battle_seq         BIGINT NOT NULL,
    event_battle_option_seq  BIGINT NOT NULL,
    member_seq               BIGINT NOT NULL,
    create_dt                DATETIME NULL,
    UNIQUE KEY uk_ebv_member_option (event_battle_seq, member_seq, event_battle_option_seq),
    KEY idx_ebv_event_member (event_battle_seq, member_seq),
    CONSTRAINT fk_ebv_event FOREIGN KEY (event_battle_seq) REFERENCES event_battle (event_battle_seq) ON DELETE CASCADE,
    CONSTRAINT fk_ebv_option FOREIGN KEY (event_battle_option_seq) REFERENCES event_battle_option (event_battle_option_seq),
    CONSTRAINT fk_ebv_member FOREIGN KEY (member_seq) REFERENCES member (member_seq)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 기존 event_battle_bet 에 event_battle_option_seq 없음 → CREATE IF NOT EXISTS 가 테이블을 바꾸지 않음
SET @prjt_schema := DATABASE();
SET @sql_ebb_opt := (SELECT IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @prjt_schema AND TABLE_NAME = 'event_battle_bet' AND COLUMN_NAME = 'event_battle_option_seq') = 0,
    'ALTER TABLE event_battle_bet ADD COLUMN event_battle_option_seq BIGINT NULL AFTER event_battle_seq',
    'SELECT 1'));
PREPARE _prjt_stmt FROM @sql_ebb_opt;
EXECUTE _prjt_stmt;
DEALLOCATE PREPARE _prjt_stmt;
SET @has_ebb_side := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = @prjt_schema AND TABLE_NAME = 'event_battle_bet' AND COLUMN_NAME = 'side');
SET @sql_ebb_m1 := IF(@has_ebb_side > 0,
    'UPDATE event_battle_bet b INNER JOIN event_battle_option o ON o.event_battle_seq = b.event_battle_seq AND o.sort_order = 1 SET b.event_battle_option_seq = o.event_battle_option_seq WHERE b.side = ''LEFT'' AND (b.event_battle_option_seq IS NULL)',
    'SELECT 1');
PREPARE _prjt_stmt FROM @sql_ebb_m1;
EXECUTE _prjt_stmt;
DEALLOCATE PREPARE _prjt_stmt;
SET @sql_ebb_m2 := IF(@has_ebb_side > 0,
    'UPDATE event_battle_bet b INNER JOIN event_battle_option o ON o.event_battle_seq = b.event_battle_seq AND o.sort_order = 2 SET b.event_battle_option_seq = o.event_battle_option_seq WHERE b.side = ''RIGHT'' AND (b.event_battle_option_seq IS NULL)',
    'SELECT 1');
PREPARE _prjt_stmt FROM @sql_ebb_m2;
EXECUTE _prjt_stmt;
DEALLOCATE PREPARE _prjt_stmt;
-- 구 스키마 NOT NULL side 가 남아 있으면 INSERT 시 필수가 되어 실패 → 컬럼 제거(위 UPDATE 로 이전된 행은 event_battle_option_seq 사용)
SET @sql_ebb_drop_side := (SELECT IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @prjt_schema AND TABLE_NAME = 'event_battle_bet' AND COLUMN_NAME = 'side') > 0,
    'ALTER TABLE event_battle_bet DROP COLUMN side',
    'SELECT 1'));
PREPARE _prjt_stmt FROM @sql_ebb_drop_side;
EXECUTE _prjt_stmt;
DEALLOCATE PREPARE _prjt_stmt;
-- 회원당 이벤트당 여러 행(베팅 실행마다 1행) → 구 UNIQUE 제거
SET @sql_ebb_drop_uk := (SELECT IF(
    (SELECT COUNT(*) FROM information_schema.STATISTICS
     WHERE TABLE_SCHEMA = @prjt_schema AND TABLE_NAME = 'event_battle_bet' AND INDEX_NAME = 'uk_ebb_event_member') > 0,
    'ALTER TABLE event_battle_bet DROP INDEX uk_ebb_event_member',
    'SELECT 1'));
PREPARE _prjt_stmt FROM @sql_ebb_drop_uk;
EXECUTE _prjt_stmt;
DEALLOCATE PREPARE _prjt_stmt;
SET @sql_ebb_idx := (SELECT IF(
    (SELECT COUNT(*) FROM information_schema.STATISTICS
     WHERE TABLE_SCHEMA = @prjt_schema AND TABLE_NAME = 'event_battle_bet' AND INDEX_NAME = 'idx_ebb_event_member') = 0,
    'ALTER TABLE event_battle_bet ADD INDEX idx_ebb_event_member (event_battle_seq, member_seq)',
    'SELECT 1'));
PREPARE _prjt_stmt FROM @sql_ebb_idx;
EXECUTE _prjt_stmt;
DEALLOCATE PREPARE _prjt_stmt;

INSERT INTO MENU (MENU_CODE, MENU_NAME, MENU_PATH, PARENT_MENU_ID, SORT_ORDER, USE_YN, CRT_DT, UPD_DT)
SELECT 'EVENT_BATTLE', '이벤트 대결', '/event-battles', NULL, 30, 'Y', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM MENU WHERE MENU_CODE = 'EVENT_BATTLE' LIMIT 1);

INSERT INTO ROLE_MENU (ROLE_ID, MENU_ID, CAN_READ, CAN_CREATE, CAN_UPDATE, CAN_DELETE, USE_YN, CRT_DT, UPD_DT)
SELECT r.ROLE_ID, m.MENU_ID, 'Y', 'Y', 'Y', 'Y', 'Y', NOW(), NOW()
FROM ROLE r
CROSS JOIN MENU m
WHERE r.ROLE_CODE = 'ADMIN'
  AND m.MENU_CODE = 'EVENT_BATTLE'
  AND NOT EXISTS (
    SELECT 1 FROM ROLE_MENU rm
    WHERE rm.ROLE_ID = r.ROLE_ID AND rm.MENU_ID = m.MENU_ID
  );

INSERT INTO ROLE (ROLE_CODE, ROLE_NAME, USE_YN, CRT_DT, UPD_DT)
SELECT 'STREAMER', '스트리머', 'Y', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM ROLE WHERE ROLE_CODE = 'STREAMER' LIMIT 1);

INSERT INTO ROLE_MENU (ROLE_ID, MENU_ID, CAN_READ, CAN_CREATE, CAN_UPDATE, CAN_DELETE, USE_YN, CRT_DT, UPD_DT)
SELECT r.ROLE_ID, m.MENU_ID, 'Y', 'Y', 'N', 'N', 'Y', NOW(), NOW()
FROM ROLE r
CROSS JOIN MENU m
WHERE r.ROLE_CODE = 'STREAMER'
  AND m.MENU_CODE = 'EVENT_BATTLE'
  AND NOT EXISTS (
    SELECT 1 FROM ROLE_MENU rm
    WHERE rm.ROLE_ID = r.ROLE_ID AND rm.MENU_ID = m.MENU_ID
  );

INSERT INTO ROLE_MENU (ROLE_ID, MENU_ID, CAN_READ, CAN_CREATE, CAN_UPDATE, CAN_DELETE, USE_YN, CRT_DT, UPD_DT)
SELECT r.ROLE_ID, m.MENU_ID, 'Y', 'N', 'N', 'N', 'Y', NOW(), NOW()
FROM ROLE r
CROSS JOIN MENU m
WHERE r.ROLE_CODE = 'USER'
  AND m.MENU_CODE = 'EVENT_BATTLE'
  AND NOT EXISTS (
    SELECT 1 FROM ROLE_MENU rm
    WHERE rm.ROLE_ID = r.ROLE_ID AND rm.MENU_ID = m.MENU_ID
  );

-- 공통 첨부파일 메타 (업로드 위치 추적: menu_url)
CREATE TABLE IF NOT EXISTS attach_file (
    file_seq       BIGINT AUTO_INCREMENT PRIMARY KEY,
    original_name  VARCHAR(500)  NOT NULL,
    stored_path    VARCHAR(1000) NOT NULL COMMENT 'upload-dir 기준 상대 경로',
    content_type   VARCHAR(200)  NULL,
    file_size      BIGINT        NULL,
    menu_url       VARCHAR(500)  NULL COMMENT '업로드 시점 화면 경로(예: /boards/write)',
    member_seq     BIGINT        NULL,
    create_id      VARCHAR(50)   NULL,
    create_ip      VARCHAR(45)   NULL,
    create_dt      DATETIME      NULL,
    use_yn         CHAR(1)       NOT NULL DEFAULT 'Y',
    KEY idx_attach_file_menu_url (menu_url),
    KEY idx_attach_file_create_dt (create_dt)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 회원 프로필 이미지 파일 참조: member.profile_image_file_seq → attach_file (수동 실행)
-- src/main/resources/db/manual/alter-member-profile-image-file-seq.sql

-- 회원 스트리머·컴퍼니 확장 프로필 (member 1:1, 코어 계정 정보와 분리)
CREATE TABLE IF NOT EXISTS member_streamer_profile (
    member_streamer_profile_seq BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_seq              BIGINT       NOT NULL,
    instagram_url           VARCHAR(500) NULL COMMENT '인스타그램 URL',
    youtube_url             VARCHAR(500) NULL COMMENT '유튜브 URL',
    soop_channel_url        VARCHAR(500) NULL COMMENT 'SOOP 방송국 URL',
    company_category_code   VARCHAR(100) NULL COMMENT '속한 컴퍼니/팀 공통코드 code_value',
    blood_type              VARCHAR(10)  NULL COMMENT '혈액형',
    career_history          LONGTEXT     NULL COMMENT '약력·이력',
    create_dt               DATETIME     NULL,
    modify_dt               DATETIME     NULL,
    UNIQUE KEY uk_msp_member (member_seq),
    CONSTRAINT fk_msp_member FOREIGN KEY (member_seq) REFERENCES member (member_seq) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 회원 포인트·티켓(아이언~다이아) 지갑
CREATE TABLE IF NOT EXISTS member_wallet (
    member_seq   BIGINT   NOT NULL PRIMARY KEY,
    point_balance BIGINT  NOT NULL DEFAULT 0,
    iron_qty     INT      NOT NULL DEFAULT 0,
    silver_qty   INT      NOT NULL DEFAULT 0,
    gold_qty     INT      NOT NULL DEFAULT 0,
    diamond_qty  INT      NOT NULL DEFAULT 0,
    modify_dt    DATETIME NULL,
    CONSTRAINT fk_mw_member FOREIGN KEY (member_seq) REFERENCES member (member_seq) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS member_wallet_ledger (
    ledger_seq   BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_seq   BIGINT       NOT NULL,
    reason_code  VARCHAR(40)  NOT NULL,
    summary      VARCHAR(500) NULL,
    point_delta  BIGINT       NOT NULL DEFAULT 0,
    iron_delta   INT          NOT NULL DEFAULT 0,
    silver_delta INT          NOT NULL DEFAULT 0,
    gold_delta   INT          NOT NULL DEFAULT 0,
    diamond_delta INT         NOT NULL DEFAULT 0,
    create_dt    DATETIME     NOT NULL,
    create_id    VARCHAR(50)  NULL,
    KEY idx_mwl_member_dt (member_seq, ledger_seq),
    CONSTRAINT fk_mwl_member FOREIGN KEY (member_seq) REFERENCES member (member_seq) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- OAuth(google/naver/kakao) 연동 회원 식별
SET @prjt_schema := DATABASE();

SET @sql_mem_oauth_p := (SELECT IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @prjt_schema AND TABLE_NAME = 'member' AND COLUMN_NAME = 'oauth_provider') = 0,
    'ALTER TABLE member ADD COLUMN oauth_provider VARCHAR(20) NULL COMMENT ''GOOGLE | NAVER | KAKAO''',
    'SELECT 1'));
PREPARE _prjt_stmt FROM @sql_mem_oauth_p;
EXECUTE _prjt_stmt;
DEALLOCATE PREPARE _prjt_stmt;

SET @sql_mem_oauth_s := (SELECT IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @prjt_schema AND TABLE_NAME = 'member' AND COLUMN_NAME = 'oauth_subject') = 0,
    'ALTER TABLE member ADD COLUMN oauth_subject VARCHAR(255) NULL COMMENT ''제공자 쪽 고유 ID (sub 등)''',
    'SELECT 1'));
PREPARE _prjt_stmt FROM @sql_mem_oauth_s;
EXECUTE _prjt_stmt;
DEALLOCATE PREPARE _prjt_stmt;

SET @sql_mem_oauth_dt := (SELECT IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @prjt_schema AND TABLE_NAME = 'member' AND COLUMN_NAME = 'oauth_sync_dt') = 0,
    'ALTER TABLE member ADD COLUMN oauth_sync_dt DATETIME NULL COMMENT ''마지막 OAuth 프로필 동기화''',
    'SELECT 1'));
PREPARE _prjt_stmt FROM @sql_mem_oauth_dt;
EXECUTE _prjt_stmt;
DEALLOCATE PREPARE _prjt_stmt;

SET @sql_mem_oauth_uk := (SELECT IF(
    (SELECT COUNT(*) FROM information_schema.statistics
     WHERE TABLE_SCHEMA = @prjt_schema AND TABLE_NAME = 'member' AND INDEX_NAME = 'uk_member_oauth') = 0,
    'ALTER TABLE member ADD UNIQUE KEY uk_member_oauth (oauth_provider, oauth_subject)',
    'SELECT 1'));
PREPARE _prjt_stmt FROM @sql_mem_oauth_uk;
EXECUTE _prjt_stmt;
DEALLOCATE PREPARE _prjt_stmt;

SET @sql_mem_last_login_dt := (SELECT IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @prjt_schema AND TABLE_NAME = 'member' AND COLUMN_NAME = 'last_login_dt') = 0,
    'ALTER TABLE member ADD COLUMN last_login_dt DATETIME NULL COMMENT ''마지막 로그인 시각''',
    'SELECT 1'));
PREPARE _prjt_stmt FROM @sql_mem_last_login_dt;
EXECUTE _prjt_stmt;
DEALLOCATE PREPARE _prjt_stmt;

SET @sql_mem_last_login_ip := (SELECT IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = @prjt_schema AND TABLE_NAME = 'member' AND COLUMN_NAME = 'last_login_ip') = 0,
    'ALTER TABLE member ADD COLUMN last_login_ip VARCHAR(45) NULL COMMENT ''마지막 로그인 IP''',
    'SELECT 1'));
PREPARE _prjt_stmt FROM @sql_mem_last_login_ip;
EXECUTE _prjt_stmt;
DEALLOCATE PREPARE _prjt_stmt;
