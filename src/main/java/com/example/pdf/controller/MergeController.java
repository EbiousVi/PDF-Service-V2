package com.example.pdf.controller;

import com.example.pdf.domain.enums.ServiceType;
import com.example.pdf.exception.PdfServicesException;
import com.example.pdf.exception.StorageException;
import com.example.pdf.exception.BadRequestException;
import com.example.pdf.service.pdf.impl.MergeServiceImpl;
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
@RequestMapping("/merge-service")
public class MergeController {
    private final MergeServiceImpl mergeService;
    private final StorageServiceImpl storageService;

    @Autowired
    public MergeController(MergeServiceImpl mergeService, StorageServiceImpl storageService) {
        this.mergeService = mergeService;
        this.storageService = storageService;
    }

    @PostMapping("/upload")
    public List<String> uploadFile(@RequestParam("file") MultipartFile[] files) throws StorageException, PdfServicesException {
        List<Path> uploadedFiles = storageService.saveUploadedFiles(files, ServiceType.MERGE);
        return mergeService.renderImgToFront(uploadedFiles)
                .stream()
                .map(path -> MvcUriComponentsBuilder.fromMethodName(ResourceController.class, "serveFile",
                        path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList());
    }

    @PostMapping("/merge")
    public ResponseEntity<Resource> splitBySelectedPages(@RequestBody Integer[] order) throws StorageException, BadRequestException, PdfServicesException {
        if (order.length == 0) throw new BadRequestException("Missing selected cards number from frontend");
        Path splittedFilePath = mergeService.merge(order);
        Resource resource = storageService.loadAsResource(splittedFilePath);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filenames=\"" + resource.getFilename() + "\"").body(resource);
    }
}
