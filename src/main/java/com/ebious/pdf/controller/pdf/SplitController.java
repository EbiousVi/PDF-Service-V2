package com.ebious.pdf.controller.pdf;

import com.ebious.pdf.domain.enums.DirName;
import com.ebious.pdf.service.pdf.interfaces.PdfSplitter;
import com.ebious.pdf.service.storage.FileStorage;
import com.ebious.pdf.service.zip.Archiver;
import com.ebious.pdf.validator.annotations.RegistryId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/split-service")
@Validated
public class SplitController {

    private final FileStorage fileStorage;
    private final PdfSplitter pdfSplitter;
    private final Archiver zipArchiver;

    @Autowired
    public SplitController(PdfSplitter pdfSplitter, FileStorage fileStorage, Archiver zipArchiver) {
        this.pdfSplitter = pdfSplitter;
        this.fileStorage = fileStorage;
        this.zipArchiver = zipArchiver;
    }

    @PostMapping("/split-by-selected-pages")
    public ResponseEntity<Resource> splitBySelectedPages(
            @RequestHeader(name = "Authorization") @NotEmpty @RegistryId String registryId,
            @RequestBody @NotEmpty List<Integer> selectedPages) {
        Path split = pdfSplitter.splitBySelectedPages(registryId, selectedPages);
        Resource resource = fileStorage.findResource(split);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filenames=\"" + resource.getFilename() + "\"").body(resource);
    }

    @GetMapping("/split-by-single-pages")
    public ResponseEntity<Resource> splitBySinglePages(@RequestHeader(name = "Authorization") @RegistryId String registryId) {
        pdfSplitter.splitBySinglePage(registryId);
        Path zipPath = zipArchiver.archive(registryId, DirName.SPLIT_ALL);
        Resource resource = fileStorage.findResource(zipPath);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filenames=\"" + resource.getFilename() + "\"").body(resource);
    }
}
