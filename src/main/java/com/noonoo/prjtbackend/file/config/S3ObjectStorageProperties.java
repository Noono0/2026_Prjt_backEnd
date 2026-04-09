package com.noonoo.prjtbackend.file.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * S3 호환 API 설정. AWS S3 외에 Cloudflare R2, Lightsail Object Storage 등 동일 SDK로 연결.
 * <ul>
 *     <li>R2: {@code endpoint}=https://&lt;accountId&gt;.r2.cloudflarestorage.com, {@code path-style-access}=true,
 *         {@code region} 은 보통 {@code auto}</li>
 *     <li>Lightsail Object Storage: 콘솔의 S3 호환 endpoint·리전·자격증명 사용</li>
 * </ul>
 * 자격: {@code access-key}/{@code secret-key} 비우면 환경변수(AWS_ACCESS_KEY_ID 등)/IAM 역할 사용.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "app.file.s3")
public class S3ObjectStorageProperties {

    /** 예: ap-northeast-2, auto(R2) */
    private String region = "ap-northeast-2";

    /** 버킷명 */
    private String bucket = "";

    /**
     * 비우면 AWS 기본 엔드포인트. R2·Lightsail 등은 콘솔에 안내된 S3 API URL.
     */
    private String endpoint = "";

    /** 객체 키 접두사 (예: prjt/files). 끝 슬래시 없이도 됨 */
    private String keyPrefix = "";

    /**
     * R2·일부 호환 스토리지는 path-style 필요.
     */
    private boolean pathStyleAccess = false;

    private String accessKey = "";
    private String secretKey = "";
}
