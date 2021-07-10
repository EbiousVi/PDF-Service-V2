package com.example.pdf.controller;

import com.example.pdf.domain.enums.ServiceType;
import com.example.pdf.exception.PdfServicesException;
import com.example.pdf.exception.StorageException;
import com.example.pdf.exception.BadRequestException;
import com.example.pdf.service.pdf.impl.SplitServiceImpl;
import com.example.pdf.service.storage.StorageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/split-service")
public class SplitController {
    private final StorageServiceImpl storageService;
    private final SplitServiceImpl splitService;

    @Autowired
    public SplitController(SplitServiceImpl splitService, StorageServiceImpl storageService) {
        this.splitService = splitService;
        this.storageService = storageService;
    }

    @PostMapping("/split-by-selected-pages")
    public ResponseEntity<Resource> splitBySelectedPages(@RequestBody Integer[] selectedPages) throws StorageException, BadRequestException {
        if (selectedPages.length == 0) throw new BadRequestException("Missing selected page number from frontend");
        Path splittedFilePath = splitService.splitBySelectedPages(selectedPages);
        Resource resource = storageService.loadAsResource(splittedFilePath);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filenames=\"" + resource.getFilename() + "\"").body(resource);
    }

    @PostMapping("/upload")
    public List<String> uploadFile(@RequestBody MultipartFile file) throws StorageException, PdfServicesException {
        Path uploadedFile = storageService.saveUploadedFile(file, ServiceType.SPLIT);
        return splitService.renderImgToFront(uploadedFile)
                .stream()
                .map(path -> MvcUriComponentsBuilder.fromMethodName(ResourceController.class, "serveFile",
                        path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList());
    }

    @GetMapping("/split-all")
    public ResponseEntity<Resource> splitAll() throws StorageException, PdfServicesException {
        Path zipPath = splitService.getZipOfPages();
        Resource resource = storageService.loadAsResource(zipPath);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filenames=\"" + resource.getFilename() + "\"").body(resource);
    }
}
