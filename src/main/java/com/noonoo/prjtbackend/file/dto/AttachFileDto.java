package com.noonoo.prjtbackend.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachFileDto {
    private Long fileSeq;
    private String originalName;
    private String storedPath;
    private String contentType;
    private Long fileSize;
    /** 업로드 시점 브라우저 경로(메뉴/화면 URL) */
    private String menuUrl;
    private Long memberSeq;
    private String createId;
    private String createIp;
    private String createDt;
    private String useYn;
}
