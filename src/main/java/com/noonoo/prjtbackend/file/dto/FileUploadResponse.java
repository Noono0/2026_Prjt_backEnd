package com.noonoo.prjtbackend.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {
    private Long fileSeq;
    /** img src 등에 넣을 절대/프록시 URL */
    private String fileUrl;
    /** Content-Disposition 다운로드용 URL */
    private String downloadUrl;
    private String originalName;
    private String contentType;
    private Long fileSize;
    private String menuUrl;
}
