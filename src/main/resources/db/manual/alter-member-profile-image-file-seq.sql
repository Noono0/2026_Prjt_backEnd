-- 회원 프로필 이미지: attach_file.file_seq 참조 (기존 PROFILE_IMAGE_URL 레거시·호환용 유지)
-- 이미 컬럼이 있으면 오류가 나므로 1회만 실행하세요.

ALTER TABLE member
    ADD COLUMN profile_image_file_seq BIGINT NULL COMMENT 'attach_file.file_seq' AFTER profile_image_url;

ALTER TABLE member
    ADD CONSTRAINT fk_member_profile_image_file
        FOREIGN KEY (profile_image_file_seq) REFERENCES attach_file (file_seq) ON DELETE SET NULL;
