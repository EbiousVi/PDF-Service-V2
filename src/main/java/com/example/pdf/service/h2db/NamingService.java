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
        System.out.println(namespace);
        System.out.println(namespace.getFilenames());
        Optional<Filename> filename = namespace.getFilenames()
                .stream()
                .filter(f -> f.getName().equals(addFormDto.getFilename()))
                .findAny();
        if (filename.isPresent()) {
            throw new CustomDBException("Filename <" + addFormDto.getFilename() + "> at namespace <" + addFormDto.getNamespace() + "> already exist");
        } else {
            filenameService.addFilename(new Filename(addFormDto.getFilename(), namespace));
        }
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

    public void personal(Map<String, String[]> map) throws CustomDBException {
        for (Map.Entry<String, String[]> pair : map.entrySet()) {
            String namespace = pair.getKey();
            namespaceService.addNamespace(namespace);
            String[] filenames = pair.getValue();
            for (String filename : filenames) {
                AddFormDto addFormDto = new AddFormDto(namespace, filename);
                this.addFilenameToNamespace(addFormDto);
            }
        }
    }
}
