package com.example.pdf.service.storage;

import com.example.pdf.domain.enums.ServiceType;
import com.example.pdf.exception.StorageException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;

public interface StorageService {
    void initStorage() throws StorageException;

    void deleteStorage();

    Resource loadAsResource(String filename) throws StorageException;

    Resource loadAsResource(Path path) throws StorageException;

    Path saveUploadedFile(MultipartFile file, ServiceType serviceType) throws StorageException;

    List<Path> saveUploadedFiles(List<MultipartFile> files, ServiceType serviceType) throws StorageException;
}
