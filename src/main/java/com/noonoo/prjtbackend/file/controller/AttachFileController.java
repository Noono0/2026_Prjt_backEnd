package com.noonoo.prjtbackend.file.controller;

import com.noonoo.prjtbackend.common.api.ApiResponse;
import com.noonoo.prjtbackend.file.dto.AttachFileDto;
import com.noonoo.prjtbackend.file.dto.FileUploadResponse;
import com.noonoo.prjtbackend.file.mapper.AttachFileMapper;
import com.noonoo.prjtbackend.file.model.ImageUploadPurpose;
import com.noonoo.prjtbackend.file.service.AttachFileService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class AttachFileController {

    private final AttachFileService attachFileService;
    private final AttachFileMapper attachFileMapper;

    @Value("${app.file.upload-dir:upload-data/files}")
    private String uploadDir;

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
            return ResponseEntity.notFound().build();
        }
        Path base = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path filePath = base.resolve(meta.getStoredPath()).normalize();
        if (!filePath.startsWith(base)) {
            return ResponseEntity.badRequest().build();
        }
        if (!Files.isReadable(filePath)) {
            return ResponseEntity.notFound().build();
        }
        FileSystemResource resource = new FileSystemResource(filePath.toFile());
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
