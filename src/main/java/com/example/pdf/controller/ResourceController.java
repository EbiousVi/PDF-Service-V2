package com.example.pdf.controller;

import com.example.pdf.exception.StorageException;
import com.example.pdf.service.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResourceController {
    private final StorageService storageService;

    @Autowired
    public ResourceController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/file/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) throws StorageException {
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filenames=\"" + file.getFilename() + "\"").body(file);
    }
}
