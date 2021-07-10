package com.example.pdf.service.storage;

import com.example.pdf.domain.enums.ServiceType;
import com.example.pdf.exception.StorageException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface StorageService {
    void init() throws StorageException;

    void deleteAll();

    Resource loadAsResource(String filename) throws StorageException;

    Resource loadAsResource(Path path) throws StorageException;

    Path saveUploadedFile(MultipartFile file, ServiceType serviceType) throws StorageException;
}
