package com.ebious.pdf.service.naming.importer;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileImporter<T> {
    List<T> importFromFile(MultipartFile file);
}
