package com.noonoo.prjtbackend.file.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
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
}
