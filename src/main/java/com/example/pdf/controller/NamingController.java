package com.example.pdf.controller;

import com.example.pdf.domain.dto.AddFormDto;
import com.example.pdf.domain.entity.Namespace;
import com.example.pdf.exception.CustomDBException;
import com.example.pdf.service.h2db.FilenameService;
import com.example.pdf.service.h2db.NamespaceService;
import com.example.pdf.service.h2db.NamingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/naming")
@Validated
public class NamingController {
    private final FilenameService filenameService;
    private final NamespaceService namespaceService;
    private final NamingService namingService;

    @Autowired
    public NamingController(FilenameService filenameService, NamespaceService namespaceService, NamingService namingService) {
        this.filenameService = filenameService;
        this.namespaceService = namespaceService;
        this.namingService = namingService;
    }

    @GetMapping("/filenames-by-namespace")
    public List<String> getFilenamesByNamespace(@RequestParam("namespace")
                                                @NotBlank(message = "{namespace.blank}")
                                                @Size(min = 3, max = 255, message = "{namespace.size}")
                                                        String name) throws CustomDBException {
        return namespaceService.getFilenamesByNamespace(name);
    }

    @GetMapping("/namespace")
    public Namespace getNamespaceByName(@RequestParam("namespace")
                                        @NotBlank(message = "{namespace.blank}")
                                        @Size(min = 3, max = 255, message = "{namespace.size}")
                                                String name) throws CustomDBException {
        return namespaceService.getNamespaceByName(name);
    }

    @GetMapping("/namespaces")
    public List<String> getAllNamespaces() {
        return namespaceService.getAllNamespace()
                .stream()
                .map(Namespace::getName)
                .collect(Collectors.toList());
    }

    @GetMapping("/namespace-and-filenames")
    public Map<String, List<String>> getAllNamespacesAndTheirFilenames() throws CustomDBException {
        return namingService.collectAllNamespacesAndTheirFilenames();
    }

    @GetMapping("/add-namespace")
    public void addNamespace(@RequestParam("namespace")
                             @NotBlank(message = "{namespace.blank}")
                             @Size(min = 3, max = 255, message = "{namespace.size}")
                                     String name) throws CustomDBException {
        namespaceService.addNamespace(name);
    }

    @PostMapping("/add-filename")
    public void addFilenameToNamespace(@Valid @RequestBody AddFormDto addFormDto) throws CustomDBException {
        namingService.addFilenameToNamespace(addFormDto);
    }

    @DeleteMapping("/delete-namespace/{namespace}")
    public void deleteNamespace(@PathVariable("namespace")
                                @NotBlank(message = "{namespace.blank}")
                                @Size(min = 3, max = 255, message = "{namespace.size}")
                                        String name) throws CustomDBException {
        namespaceService.deleteNamespaceByName(name);
    }

    @DeleteMapping("/delete-filename/{namespace}/{filename}")
    public void deleteFilename(@PathVariable("namespace")
                               @NotBlank(message = "{namespace.blank}")
                               @Size(min = 3, max = 255, message = "{namespace.size}")
                                       String namespace,
                               @PathVariable("filename")
                               @NotBlank(message = "{filename.blank}")
                               @Size(min = 3, max = 255, message = "{filename.size}")
                                       String filename) throws CustomDBException {
        filenameService.deleteFilename(filename, namespace);
    }
}
