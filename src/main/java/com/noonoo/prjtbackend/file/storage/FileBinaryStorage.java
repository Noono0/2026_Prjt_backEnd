package com.noonoo.prjtbackend.file.storage;

import java.io.IOException;

/** 로컬 디스크 / S3·R2 등으로 교체 가능한 저장 추상화 */
public interface FileBinaryStorage {

    void save(String relativePath, byte[] data) throws IOException;
}
