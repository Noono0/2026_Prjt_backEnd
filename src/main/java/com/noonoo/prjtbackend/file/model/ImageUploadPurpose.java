package com.noonoo.prjtbackend.file.model;

/**
 * 업로드 이미지 용도 — 처리 정책(해상도·목표 용량)이 달라진다.
 */
public enum ImageUploadPurpose {
    /** 게시판 본문·일반 첨부 */
    BOARD,
    /** 프로필 등 작은 썸네일 */
    PROFILE;

    public static ImageUploadPurpose fromParam(String raw) {
        if (raw == null || raw.isBlank()) {
            return BOARD;
        }
        try {
            return ImageUploadPurpose.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("uploadPurpose 는 board 또는 profile 만 허용됩니다.");
        }
    }
}
