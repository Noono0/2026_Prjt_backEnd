package com.noonoo.prjtbackend.file.controller;

import com.noonoo.prjtbackend.common.api.ApiResponse;
import com.noonoo.prjtbackend.file.dto.AttachFileDto;
import com.noonoo.prjtbackend.file.dto.FileUploadResponse;
import com.noonoo.prjtbackend.file.mapper.AttachFileMapper;
import com.noonoo.prjtbackend.file.model.ImageUploadPurpose;
import com.noonoo.prjtbackend.file.service.AttachFileService;
import com.noonoo.prjtbackend.file.storage.FileBinaryStorage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;

@Slf4j
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class AttachFileController {

    private final AttachFileService attachFileService;
    private final AttachFileMapper attachFileMapper;
    private final FileBinaryStorage fileBinaryStorage;

    @Value("${app.file.public-base-url:}")
    private String configuredPublicBaseUrl;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<FileUploadResponse> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "menuUrl", required = false) String menuUrl,
            @RequestParam(value = "uploadPurpose", required = false, defaultValue = "board") String uploadPurposeRaw,
            HttpServletRequest request
    ) {
        String base = resolvePublicBaseUrl(request);
        ImageUploadPurpose purpose = ImageUploadPurpose.fromParam(uploadPurposeRaw);
        FileUploadResponse data = attachFileService.store(file, menuUrl, base, purpose);
        return ApiResponse.ok("파일이 업로드되었습니다.", data);
    }

    @GetMapping("/view/{fileSeq}")
    public ResponseEntity<Resource> view(@PathVariable Long fileSeq) {
        return buildFileResponse(fileSeq, false);
    }

    @GetMapping("/download/{fileSeq}")
    public ResponseEntity<Resource> download(@PathVariable Long fileSeq) {
        return buildFileResponse(fileSeq, true);
    }

    private ResponseEntity<Resource> buildFileResponse(Long fileSeq, boolean attachment) {
        AttachFileDto meta = attachFileMapper.selectByFileSeq(fileSeq);
        if (meta == null) {
            log.warn(
                    "[attach-file] 조회 실패 | reason=attach_file_메타_없음_DB | fileSeq={} 힌트=fileSeq_오타_또는_DB_롤백 ",
                    fileSeq
            );
            return ResponseEntity.notFound().build();
        }
        final byte[] data;
        try {
            data = fileBinaryStorage.load(meta.getStoredPath());
        } catch (NoSuchFileException | FileNotFoundException e) {
            log.warn(
                    "[attach-file] 조회 실패 | reason=저장소_객체_없음_경로불일치_또는_S3키누락 | fileSeq={} storedPath={} err={} 힌트=로컬이면_upload-dir_운영이면_S3_버킷키_prefix ",
                    fileSeq,
                    meta.getStoredPath(),
                    e.getMessage()
            );
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            log.warn(
                    "[attach-file] 조회 실패 | reason=저장소_읽기_IO | fileSeq={} storedPath={} err={} ",
                    fileSeq,
                    meta.getStoredPath(),
                    e.getMessage(),
                    e
            );
            return ResponseEntity.internalServerError().build();
        }
        if (log.isDebugEnabled()) {
            log.debug(
                    "[attach-file] 조회 성공 | fileSeq={} storedPath={} bytes={} attachment={} ",
                    fileSeq,
                    meta.getStoredPath(),
                    data.length,
                    attachment
            );
        }
        ByteArrayResource resource = new ByteArrayResource(data);
        String contentType = StringUtils.hasText(meta.getContentType())
                ? meta.getContentType()
                : MediaType.APPLICATION_OCTET_STREAM_VALUE;

        ResponseEntity.BodyBuilder builder = ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType));

        if (attachment) {
            String name = meta.getOriginalName() != null ? meta.getOriginalName() : "file";
            String encoded = URLEncoder.encode(name, StandardCharsets.UTF_8).replace("+", "%20");
            builder.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encoded);
        } else {
            builder.header(HttpHeaders.CONTENT_DISPOSITION, "inline");
        }
        return builder.body(resource);
    }

    private String resolvePublicBaseUrl(HttpServletRequest request) {
        if (StringUtils.hasText(configuredPublicBaseUrl)) {
            return configuredPublicBaseUrl.trim();
        }
        String scheme = request.getScheme();
        String host = request.getServerName();
        int port = request.getServerPort();
        StringBuilder sb = new StringBuilder();
        sb.append(scheme).append("://").append(host);
        if (("http".equals(scheme) && port != 80) || ("https".equals(scheme) && port != 443)) {
            sb.append(":").append(port);
        }
        return sb.toString();
    }
}
