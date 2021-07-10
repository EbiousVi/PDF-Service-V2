package com.example.pdf.controller;

import com.example.pdf.domain.dto.AddFormDto;
import com.example.pdf.service.h2db.FilenameService;
import com.example.pdf.service.h2db.NamespaceService;
import com.example.pdf.service.h2db.NamingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
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

    @GetMapping("/get-filenames-by-namespace")
    public List<String> getFilenameByNamespace(@RequestParam("namespace") String namespace) {
        return namingService.getFilenamesByNamespace(namespace);
    }

    @GetMapping("/get-namespaces")
    public List<String> getAllNamespaces() {
        return namespaceService.getAllNamespace();
    }

    @GetMapping("/get-filenames")
    public Map<String, List<String>> getAll() {
        return namingService.collectAllNamespacesAndTheirFilenames();
    }

    @GetMapping("/add-namespace")
    public void addNamespace(@RequestParam("namespace") String name) {
        namespaceService.addNamespace(name);
    }

    @PostMapping("/add-filename")
    public void addFilenameToNamespace(@RequestBody AddFormDto addFormDto) {
        namingService.addFilenameToNamespace(addFormDto);
    }

    @DeleteMapping("/delete-namespace/{namespace}")
    public void deleteNamespace(@PathVariable("namespace") String name) {
        namespaceService.deleteNamespaceByName(name);
    }

    @DeleteMapping("/delete-filename/{filename}")
    public void deleteFilename(@PathVariable("filename") String name) {
        filenameService.deleteFilenameByName(name);
    }
}
