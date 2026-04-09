package com.noonoo.prjtbackend.file.config;

import com.noonoo.prjtbackend.file.storage.FileBinaryStorage;
import com.noonoo.prjtbackend.file.storage.S3CompatibleFileBinaryStorage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
@ConditionalOnProperty(name = "app.file.storage-type", havingValue = "s3")
@EnableConfigurationProperties(S3ObjectStorageProperties.class)
public class S3ObjectStorageConfig {

    @Bean
    public S3Client s3Client(S3ObjectStorageProperties props) {
        if (!StringUtils.hasText(props.getBucket())) {
            throw new IllegalStateException("app.file.s3.bucket 은 storage-type=s3 일 때 필수입니다.");
        }

        var builder = S3Client.builder()
                .region(Region.of(props.getRegion().trim()));

        if (StringUtils.hasText(props.getEndpoint())) {
            builder.endpointOverride(URI.create(props.getEndpoint().trim()));
        }

        builder.serviceConfiguration(S3Configuration.builder()
                .pathStyleAccessEnabled(props.isPathStyleAccess())
                .build());

        if (StringUtils.hasText(props.getAccessKey()) && StringUtils.hasText(props.getSecretKey())) {
            builder.credentialsProvider(StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(props.getAccessKey().trim(), props.getSecretKey().trim())
            ));
        } else {
            builder.credentialsProvider(DefaultCredentialsProvider.create());
        }

        return builder.build();
    }

    @Bean
    public FileBinaryStorage fileBinaryStorage(S3Client s3Client, S3ObjectStorageProperties props) {
        return new S3CompatibleFileBinaryStorage(s3Client, props);
    }
}
