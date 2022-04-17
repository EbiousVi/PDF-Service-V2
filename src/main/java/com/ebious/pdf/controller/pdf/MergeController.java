package com.ebious.pdf.controller.pdf;

import com.ebious.pdf.service.pdf.interfaces.PdfMerger;
import com.ebious.pdf.service.storage.FileStorage;
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
@RequestMapping("/merge-service")
@Validated
public class MergeController {

    private final PdfMerger pdfMerger;
    private final FileStorage fileStorage;

    @Autowired
    public MergeController(PdfMerger pdfMerger, FileStorage fileStorage) {
        this.pdfMerger = pdfMerger;
        this.fileStorage = fileStorage;
    }

    @PostMapping("/merge")
    public ResponseEntity<Resource> merge(@RequestHeader(name = "Authorization") @RegistryId String registryId,
                                          @RequestBody @NotEmpty List<String> filenames) {
        Path merge = pdfMerger.merge(registryId, filenames);
        Resource resource = fileStorage.findResource(merge);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filenames=\"" + resource.getFilename() + "\"").body(resource);
    }
}
