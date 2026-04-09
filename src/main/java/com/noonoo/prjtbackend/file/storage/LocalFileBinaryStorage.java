package com.noonoo.prjtbackend.file.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@ConditionalOnProperty(name = "app.file.storage-type", havingValue = "local", matchIfMissing = true)
public class LocalFileBinaryStorage implements FileBinaryStorage {

    @Value("${app.file.upload-dir:upload-data/files}")
    private String uploadDir;

    @Override
    public void save(String relativePath, byte[] data) throws IOException {
        Path base = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path target = base.resolve(relativePath).normalize();
        if (!target.startsWith(base)) {
            throw new IOException("잘못된 저장 경로입니다.");
        }
        Files.createDirectories(target.getParent());
        Files.write(target, data);
    }

    @Override
    public byte[] load(String relativePath) throws IOException {
        Path base = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path target = base.resolve(relativePath).normalize();
        if (!target.startsWith(base)) {
            throw new IOException("잘못된 저장 경로입니다.");
        }
        if (!Files.isRegularFile(target)) {
            throw new NoSuchFileException(target.toString());
        }
        return Files.readAllBytes(target);
    }

    @Override
    public boolean exists(String relativePath) {
        try {
            Path base = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path target = base.resolve(relativePath).normalize();
            if (!target.startsWith(base)) {
                return false;
            }
            return Files.isRegularFile(target);
        } catch (Exception e) {
            return false;
        }
    }
}
