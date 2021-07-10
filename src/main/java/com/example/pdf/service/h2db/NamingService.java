package com.example.pdf.service.h2db;

import com.example.pdf.domain.dto.AddFormDto;
import com.example.pdf.domain.entity.Filename;
import com.example.pdf.domain.entity.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NamingService {
    private final NamespaceService namespaceService;
    private final FilenameService filenameService;

    @Autowired
    public NamingService(NamespaceService namespaceService, FilenameService filenameService) {
        this.namespaceService = namespaceService;
        this.filenameService = filenameService;
    }

    public List<String> getFilenamesByNamespace(String name) {
        return filenameService.getFilenamesByNamespace(namespaceService.getNamespaceByName(name))
                .stream()
                .map(Filename::getFilename)
                .collect(Collectors.toList());
    }

    public void addFilenameToNamespace(AddFormDto addFormDto) {
        Namespace namespace = namespaceService.getNamespaceByName(addFormDto.getNamespace());
        Filename filename = new Filename();
        filename.setFilename(addFormDto.getFilename());
        filename.setNamespace(namespace);
        filenameService.addFilename(filename);
    }

    public void addFilenameToNamespace(String _namespace, String _filename) {
        Namespace namespace = namespaceService.getNamespaceByName(_namespace);
        Filename filename = new Filename();
        filename.setFilename(_filename);
        filename.setNamespace(namespace);
        filenameService.addFilename(filename);
    }

    public Map<String, List<String>> collectAllNamespacesAndTheirFilenames() {
        List<String> namespaces = namespaceService.getAllNamespace();
        Map<String, List<String>> map = new HashMap<>();
        for (String namespace : namespaces) {
            map.put(namespace, this.getFilenamesByNamespace(namespace));
        }
        return map;
    }
}
