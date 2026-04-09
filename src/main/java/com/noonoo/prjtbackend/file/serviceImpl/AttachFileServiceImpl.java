package com.noonoo.prjtbackend.file.serviceImpl;

import com.noonoo.prjtbackend.common.config.RequestContext;
import com.noonoo.prjtbackend.file.dto.AttachFileDto;
import com.noonoo.prjtbackend.file.dto.FileUploadResponse;
import com.noonoo.prjtbackend.file.dto.ImageProcessResult;
import com.noonoo.prjtbackend.file.exception.NotAnImageFileException;
import com.noonoo.prjtbackend.file.mapper.AttachFileMapper;
import com.noonoo.prjtbackend.file.model.ImageUploadPurpose;
import com.noonoo.prjtbackend.file.service.AttachFileService;
import com.noonoo.prjtbackend.file.service.ImageProcessingService;
import com.noonoo.prjtbackend.file.storage.FileBinaryStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttachFileServiceImpl implements AttachFileService {

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif"
    );
    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "webp", "gif"
    );
    private static final Set<String> DANGEROUS_EXTENSIONS = Set.of(
            "exe", "dll", "bat", "cmd", "com", "scr", "msi",
            "js", "mjs", "cjs", "vbs", "wsf", "ps1", "sh", "bash",
            "jar", "war", "class", "apk", "ipa", "reg", "hta", "lnk",
            "php", "phtml", "jsp", "jspx", "asp", "aspx", "py", "rb", "pl"
    );

    private final AttachFileMapper attachFileMapper;
    private final ImageProcessingService imageProcessingService;
    private final FileBinaryStorage fileBinaryStorage;

    @Value("${spring.servlet.multipart.max-file-size:5MB}")
    private DataSize multipartMaxFileSize;

    @Override
    @Transactional
    public FileUploadResponse store(MultipartFile file, String menuUrl, String publicBaseUrl, ImageUploadPurpose purpose) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }
        long maxBytes = multipartMaxFileSize.toBytes();
        if (file.getSize() > maxBytes) {
            throw new IllegalArgumentException(
                    "파일 크기는 " + (maxBytes / (1024 * 1024)) + "MB 이하여야 합니다. (multipart 최대 허용)");
        }

        String original = safeFileName(file.getOriginalFilename());
        String originalExt = extensionOf(original);
        if (DANGEROUS_EXTENSIONS.contains(originalExt)) {
            throw new IllegalArgumentException("위험한 확장자의 파일은 업로드할 수 없습니다.");
        }
        if (!ALLOWED_IMAGE_EXTENSIONS.contains(originalExt)) {
            throw new NotAnImageFileException("허용되지 않은 확장자입니다. (jpg, jpeg, png, webp, gif)");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new NotAnImageFileException("지원하지 않는 이미지 형식입니다.");
        }
        String normalizedType = contentType.toLowerCase(Locale.ROOT);
        if (!isExtensionCompatible(normalizedType, originalExt)) {
            throw new NotAnImageFileException("확장자와 파일 형식이 일치하지 않습니다.");
        }

        final byte[] raw;
        try {
            raw = file.getBytes();
        } catch (IOException e) {
            throw new IllegalArgumentException("파일을 읽을 수 없습니다.", e);
        }
        if (!hasValidSignatureFromBytes(raw, normalizedType)) {
            throw new NotAnImageFileException("파일 시그니처 검증에 실패했습니다.");
        }

        if (!StringUtils.hasText(original)) {
            original = "image";
        }

        ImageProcessResult processed = imageProcessingService.process(raw, normalizedType, originalExt, purpose);
        byte[] storedBytes = processed.bytes();
        String storedContentType = processed.contentType();
        String ext = processed.extension();
        long storedSize = storedBytes.length;

        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String storedName = UUID.randomUUID().toString().replace("-", "") + "." + ext;
        String relativePath = datePath + "/" + storedName;

        try {
            fileBinaryStorage.save(relativePath, storedBytes);
        } catch (IOException e) {
            log.error(
                    "[attach-file] 저장소_쓰기_실패 | relativePath={} bytes={} storage={} err={} 힌트=디스크용량_S3 creds_put_권한 ",
                    relativePath,
                    storedSize,
                    fileBinaryStorage.getClass().getSimpleName(),
                    e.getMessage(),
                    e
            );
            throw new IllegalStateException("파일 저장에 실패했습니다.", e);
        }

        Long memberSeq = RequestContext.getLoginMemberSeq();
        String loginId = RequestContext.getLoginMemberId();
        String clientIp = RequestContext.getClientIp();

        AttachFileDto row = AttachFileDto.builder()
                .originalName(original)
                .storedPath(relativePath)
                .contentType(storedContentType)
                .fileSize(storedSize)
                .menuUrl(trimMenuUrl(menuUrl))
                .memberSeq(memberSeq)
                .createId(StringUtils.hasText(loginId) ? loginId : "SYSTEM")
                .createIp(StringUtils.hasText(clientIp) ? clientIp : "0.0.0.0")
                .build();

        attachFileMapper.insertAttachFile(row);
        Long fileSeq = row.getFileSeq();
        if (fileSeq == null) {
            throw new IllegalStateException("파일 메타 저장에 실패했습니다.");
        }

        String baseUrl = publicBaseUrl.endsWith("/") ? publicBaseUrl.substring(0, publicBaseUrl.length() - 1) : publicBaseUrl;
        String fileUrl = baseUrl + "/api/files/view/" + fileSeq;
        String downloadUrl = baseUrl + "/api/files/download/" + fileSeq;

        log.info(
                "[attach-file] 업로드_완료 | fileSeq={} storedPath={} size={} memberSeq={} purpose={} publicBaseUrl={} fileUrl={} 힌트=X박스면_fileUrl_호스트가_브라우저주소와_다르면_APP_FILE_PUBLIC_BASE_URL_설정 ",
                fileSeq,
                relativePath,
                storedSize,
                memberSeq,
                purpose,
                publicBaseUrl,
                fileUrl
        );

        return FileUploadResponse.builder()
                .fileSeq(fileSeq)
                .fileUrl(fileUrl)
                .downloadUrl(downloadUrl)
                .originalName(original)
                .contentType(storedContentType)
                .fileSize(storedSize)
                .menuUrl(row.getMenuUrl())
                .imageDowngraded(processed.imageDowngraded())
                .optimizationNotice(processed.optimizationNotice())
                .build();
    }

    private static String trimMenuUrl(String menuUrl) {
        if (!StringUtils.hasText(menuUrl)) {
            return null;
        }
        String t = menuUrl.trim();
        return t.length() > 500 ? t.substring(0, 500) : t;
    }

    private static String extensionOf(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            return "";
        }
        int dot = fileName.lastIndexOf('.');
        if (dot < 0 || dot >= fileName.length() - 1) {
            return "";
        }
        return fileName.substring(dot + 1).toLowerCase(Locale.ROOT);
    }

    private static boolean isExtensionCompatible(String contentType, String extension) {
        return switch (contentType) {
            case "image/jpeg" -> "jpg".equals(extension) || "jpeg".equals(extension);
            case "image/png" -> "png".equals(extension);
            case "image/gif" -> "gif".equals(extension);
            case "image/webp" -> "webp".equals(extension);
            default -> false;
        };
    }

    private static boolean hasValidSignatureFromBytes(byte[] raw, String contentType) {
        if (raw == null || raw.length < 3) {
            return false;
        }
        int n = Math.min(16, raw.length);
        byte[] header = new byte[n];
        System.arraycopy(raw, 0, header, 0, n);
        return switch (contentType) {
            case "image/jpeg" -> header.length >= 3
                    && (header[0] & 0xFF) == 0xFF
                    && (header[1] & 0xFF) == 0xD8
                    && (header[2] & 0xFF) == 0xFF;
            case "image/png" -> header.length >= 8
                    && (header[0] & 0xFF) == 0x89
                    && header[1] == 0x50
                    && header[2] == 0x4E
                    && header[3] == 0x47
                    && (header[4] & 0xFF) == 0x0D
                    && (header[5] & 0xFF) == 0x0A
                    && (header[6] & 0xFF) == 0x1A
                    && (header[7] & 0xFF) == 0x0A;
            case "image/gif" -> header.length >= 6
                    && header[0] == 0x47
                    && header[1] == 0x49
                    && header[2] == 0x46
                    && header[3] == 0x38
                    && (header[4] == 0x37 || header[4] == 0x39)
                    && header[5] == 0x61;
            case "image/webp" -> header.length >= 12
                    && header[0] == 0x52
                    && header[1] == 0x49
                    && header[2] == 0x46
                    && header[3] == 0x46
                    && header[8] == 0x57
                    && header[9] == 0x45
                    && header[10] == 0x42
                    && header[11] == 0x50;
            default -> false;
        };
    }

    private static String safeFileName(String originalName) {
        if (!StringUtils.hasText(originalName)) {
            return "";
        }
        String cleaned = originalName.replace("\\", "/");
        int slash = cleaned.lastIndexOf('/');
        if (slash >= 0) {
            cleaned = cleaned.substring(slash + 1);
        }
        return cleaned.trim();
    }
}
