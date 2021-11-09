package com.example.pdf.controller;

import com.example.pdf.anno.annotations.IsAllPDF;
import com.example.pdf.domain.enums.ServiceType;
import com.example.pdf.exception.PdfServiceException;
import com.example.pdf.exception.StorageException;
import com.example.pdf.service.pdf.impl.MergeServiceImpl;
import com.example.pdf.service.storage.PathManager;
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
@RequestMapping("/merge-service")
@Validated
public class MergeController {
    private final MergeServiceImpl mergeService;
    private final StorageServiceImpl storageService;

    @Autowired
    public MergeController(MergeServiceImpl mergeService, StorageServiceImpl storageService) {
        this.mergeService = mergeService;
        this.storageService = storageService;
    }

    /**
     * The user upload files to be merged. When saved, files are assigned an ordinal number starting from 0.
     * The ordinal number and path to the saved file are added to {@link PathManager#uploadedFiles}.
     * The method {@link MergeServiceImpl # renderImgToFront} takes paths from the method
     * {@link StorageServiceImpl # saveUploadedFiles} in order to save and render cover for files.
     * Before displaying, the Frontend assigns a ordinal number to each cover starting from 0.
     * Which corresponds to the ordinal number at {@link PathManager#uploadedFiles}.
     * <p>
     * P.S. This is not the best solution. It might be better to add a DTO. For now, so be it.
     */
    @PostMapping("/upload")
    public List<String> uploadFiles(@RequestPart("file") @IsAllPDF List<MultipartFile> files)
            throws StorageException, PdfServiceException {
        List<Path> uploadedFiles = storageService.saveUploadedFiles(files, ServiceType.MERGE);
        return mergeService.renderFileCover(uploadedFiles)
                .stream()
                .map(path -> MvcUriComponentsBuilder
                        .fromMethodName(ResourceController.class, "serveFile",
                                path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList());
    }

    @PostMapping("/merge")
    public ResponseEntity<Resource> merge(@RequestBody @NotEmpty(message = "{order.empty}") Integer[] order)
            throws StorageException, PdfServiceException {
        Path splittedFile = mergeService.merge(order);
        Resource resource = storageService.loadAsResource(splittedFile);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filenames=\"" + resource.getFilename() + "\"").body(resource);
    }
}
