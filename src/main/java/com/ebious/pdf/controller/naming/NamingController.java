package com.ebious.pdf.controller.naming;

import com.ebious.pdf.domain.dto.AddFormDto;
import com.ebious.pdf.service.naming.NamingService;
import com.ebious.pdf.service.storage.FileStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/naming")
@Validated
public class NamingController {

    private final NamingService namingService;
    private final FileStorage FileStorage;

    @Autowired
    public NamingController(NamingService namingService, FileStorage FileStorage) {
        this.namingService = namingService;
        this.FileStorage = FileStorage;
    }

    @GetMapping("/namespaces")
    public List<String> getNamespaces() {
        return namingService.getAllNamespaceNames();
    }

    @GetMapping("/namespace-and-filenames")
    public Map<String, List<String>> getAllNamespacesAndFilenames() {
       return namingService.collectNamespacesAndFilenames();
    }

    @GetMapping("/add-namespace")
    public void addNamespace(@RequestParam("namespace")
                             @NotBlank(message = "{namespace.blank}")
                             @Size(min = 3, max = 255, message = "{namespace.size}") String name) {
        namingService.addNamespace(name);
    }


    @GetMapping("/export-to-json")
    public ResponseEntity<Resource> exportToJson() {
        Resource resource = FileStorage.findResource(namingService.exportToFile());
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filenames=\"" + resource.getFilename() + "\"").body(resource);
    }

    @PostMapping("/import-from-json")
    public void importFromJson(@RequestPart("file") MultipartFile file) {
        namingService.importFromFile(file);
    }

    @PostMapping("/add-filename")
    public void addFilenameToNamespace(@Valid @RequestBody AddFormDto addFormDto) {
        namingService.addFilenameToNamespace(addFormDto.getNamespace(), addFormDto.getFilename());
    }

    @DeleteMapping("/delete-namespace/{namespace}")
    public void deleteNamespace(@PathVariable("namespace")
                                @NotBlank(message = "{namespace.blank}")
                                @Size(min = 3, max = 255, message = "{namespace.size}") String name) {
        namingService.deleteNamespace(name);
    }

    @DeleteMapping("/delete-filename/{namespace}/{filename}")
    public void deleteFilename(@PathVariable("namespace")
                               @NotBlank(message = "{namespace.blank}")
                               @Size(min = 3, max = 255, message = "{namespace.size}")
                                       String namespace,
                               @PathVariable("filename")
                               @NotBlank(message = "{filename.blank}")
                               @Size(min = 3, max = 255, message = "{filename.size}")
                                       String filename) {
        namingService.deleteFilename(filename, namespace);
    }
}