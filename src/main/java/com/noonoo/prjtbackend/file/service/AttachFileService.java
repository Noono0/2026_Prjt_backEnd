package com.noonoo.prjtbackend.file.service;

import com.noonoo.prjtbackend.file.dto.FileUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface AttachFileService {

    FileUploadResponse store(MultipartFile file, String menuUrl, String publicBaseUrl);
}
