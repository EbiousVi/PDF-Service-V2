package com.example.pdf.controller;

import com.example.pdf.anno.annotations.IsPDF;
import com.example.pdf.domain.enums.ServiceType;
import com.example.pdf.exception.PdfServiceException;
import com.example.pdf.exception.StorageException;
import com.example.pdf.service.pdf.impl.SplitServiceImpl;
import com.example.pdf.service.storage.StorageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.validation.constraints.NotEmpty;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/split-service")
@Validated
public class SplitController {
    private final StorageServiceImpl storageService;
    private final SplitServiceImpl splitService;

    @Autowired
    public SplitController(SplitServiceImpl splitService, StorageServiceImpl storageService) {
        this.splitService = splitService;
        this.storageService = storageService;
    }

    @PostMapping("/split-by-selected-pages")
    public ResponseEntity<Resource> splitBySelectedPages
            (@RequestBody @NotEmpty(message = "{selected_pages.empty}") Integer[] selectedPages)
            throws PdfServiceException, StorageException {
        Path splittedFilePath = splitService.splitBySelectedPages(selectedPages);
        Resource resource = storageService.loadAsResource(splittedFilePath);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filenames=\"" + resource.getFilename() + "\"").body(resource);
    }

    @PostMapping("/upload")
    public List<String> uploadFile(@RequestPart("file") @IsPDF MultipartFile file)
            throws StorageException, PdfServiceException {
        Path uploadedFile = storageService.saveUploadedFile(file, ServiceType.SPLIT);
        return splitService.renderFilePages(uploadedFile)
                .stream()
                .map(path -> MvcUriComponentsBuilder
                        .fromMethodName(ResourceController.class, "serveFile",
                                path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList());
    }

    @GetMapping("/split-by-single-pages")
    public ResponseEntity<Resource> splitBySinglePages() throws StorageException, PdfServiceException {
        Path zipPath = splitService.packToZip(splitService.splitBySinglePages());
        Resource resource = storageService.loadAsResource(zipPath);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filenames=\"" + resource.getFilename() + "\"").body(resource);
    }
}
