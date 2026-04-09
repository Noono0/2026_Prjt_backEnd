package com.noonoo.prjtbackend.file.storage;

import com.noonoo.prjtbackend.file.config.S3ObjectStorageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Locale;

/**
 * AWS SDK S3 클라이언트 기반 저장. 엔드포인트만 바꿔 R2 / Lightsail Object Storage 등에 재사용.
 */
@RequiredArgsConstructor
public class S3CompatibleFileBinaryStorage implements FileBinaryStorage {

    private final S3Client s3Client;
    private final S3ObjectStorageProperties props;

    @Override
    public void save(String relativePath, byte[] data) throws IOException {
        String key = objectKey(relativePath);
        try {
            PutObjectRequest.Builder b = PutObjectRequest.builder()
                    .bucket(props.getBucket())
                    .key(key)
                    .contentType(guessContentType(relativePath));
            s3Client.putObject(b.build(), RequestBody.fromBytes(data));
        } catch (Exception e) {
            throw new IOException("S3 PutObject 실패: " + key, e);
        }
    }

    @Override
    public byte[] load(String relativePath) throws IOException {
        String key = objectKey(relativePath);
        try {
            GetObjectRequest get = GetObjectRequest.builder()
                    .bucket(props.getBucket())
                    .key(key)
                    .build();
            ResponseBytes<?> rb = s3Client.getObjectAsBytes(get);
            return rb.asByteArray();
        } catch (NoSuchKeyException e) {
            throw new java.io.FileNotFoundException(key);
        } catch (Exception e) {
            throw new IOException("S3 GetObject 실패: " + key, e);
        }
    }

    @Override
    public boolean exists(String relativePath) {
        String key = objectKey(relativePath);
        try {
            s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(props.getBucket())
                    .key(key)
                    .build());
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private String objectKey(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            throw new IllegalArgumentException("relativePath 비어 있음");
        }
        String normalized = relativePath.replace("\\", "/").replaceAll("^/+", "");
        String prefix = props.getKeyPrefix() == null ? "" : props.getKeyPrefix().trim().replace("\\", "/").replaceAll("^/+", "").replaceAll("/+$", "");
        if (prefix.isEmpty()) {
            return normalized;
        }
        return prefix + "/" + normalized;
    }

    private static String guessContentType(String relativePath) {
        String ext = extensionOf(relativePath).toLowerCase(Locale.ROOT);
        if ("jpg".equals(ext) || "jpeg".equals(ext)) {
            return "image/jpeg";
        }
        if ("png".equals(ext)) {
            return "image/png";
        }
        if ("gif".equals(ext)) {
            return "image/gif";
        }
        if ("webp".equals(ext)) {
            return "image/webp";
        }
        return "application/octet-stream";
    }

    private static String extensionOf(String path) {
        int slash = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
        String name = slash >= 0 ? path.substring(slash + 1) : path;
        int dot = name.lastIndexOf('.');
        if (dot < 0 || dot >= name.length() - 1) {
            return "";
        }
        return name.substring(dot + 1);
    }
}
