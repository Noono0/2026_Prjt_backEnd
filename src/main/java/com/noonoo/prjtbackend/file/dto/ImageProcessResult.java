package com.noonoo.prjtbackend.file.dto;

/**
 * 서버에서 웹용으로 변환한 최종 바이트. 원본은 저장하지 않는다.
 */
public record ImageProcessResult(
        byte[] bytes,
        String contentType,
        String extension,
        boolean imageDowngraded,
        String optimizationNotice
) {}
