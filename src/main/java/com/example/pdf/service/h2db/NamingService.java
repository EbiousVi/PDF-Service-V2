package com.example.pdf.service.h2db;

import com.example.pdf.domain.dto.AddFormDto;
import com.example.pdf.domain.entity.Filename;
import com.example.pdf.domain.entity.Namespace;
import com.example.pdf.exception.CustomDBException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    public void addFilenameToNamespace(AddFormDto addFormDto) throws CustomDBException {
        Namespace namespace = namespaceService.getNamespaceByName(addFormDto.getNamespace());
        Optional<Filename> filename = namespace.getFilenames()
                .stream()
                .filter(f -> f.getName().equals(addFormDto.getFilename()))
                .findAny();
        if (filename.isPresent()) {
            throw new CustomDBException("Filename at namespace = " + namespace.getName() + " already exist");
        } else {
            filenameService.addFilename(new Filename(addFormDto.getFilename(), namespace));
        }
    }

    public void addFilenameToNamespace(String _namespace, String _filename) throws CustomDBException {
        Namespace namespace = namespaceService.getNamespaceByName(_namespace);
        filenameService.addFilename(new Filename(_filename, namespace));
    }

    public Map<String, List<String>> collectAllNamespacesAndTheirFilenames() {
        return namespaceService.getAllNamespace()
                .stream()
                .collect(Collectors.toMap(Namespace::getName,
                        n -> n.getFilenames()
                        .stream()
                        .map(Filename::getName)
                        .collect(Collectors.toList())));
    }
}
