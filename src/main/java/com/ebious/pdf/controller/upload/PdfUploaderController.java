package com.ebious.pdf.controller.upload;

import com.ebious.pdf.domain.dto.UploadedDto;
import com.ebious.pdf.service.pdf.interfaces.PdfRender;
import com.ebious.pdf.service.storage.FileStorage;
import com.ebious.pdf.service.uploader.UploadedDtoService;
import com.ebious.pdf.validator.annotations.PdfFile;
import com.ebious.pdf.validator.annotations.PdfFiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/uploader")
@Validated
public class PdfUploaderController {

    private final FileStorage fileStorage;
    private final PdfRender pdfRender;
    private final UploadedDtoService uploadedDtoService;

    @Autowired
    public PdfUploaderController(FileStorage fileStorage, PdfRender pdfRender,
                                 UploadedDtoService uploadedDtoService) {
        this.fileStorage = fileStorage;
        this.pdfRender = pdfRender;
        this.uploadedDtoService = uploadedDtoService;
    }

    @PostMapping("/pdf-file")
    public UploadedDto uploadSinglePdf(@RequestPart("file") @PdfFile MultipartFile file) {
        String registryId = fileStorage.saveFile(file);
        List<Path> renders = pdfRender.renderPageCovers(registryId);
        return uploadedDtoService.prepareUploadedDto(registryId, renders);
    }

    @PostMapping("/pdf-files")
    public UploadedDto uploadMultiplePdf(@RequestPart("file") @PdfFiles List<MultipartFile> files) {
        String registryId = fileStorage.saveFiles(files);
        List<Path> renders = pdfRender.renderFileCovers(registryId);
        return uploadedDtoService.prepareUploadedDto(registryId, renders);
    }
}
