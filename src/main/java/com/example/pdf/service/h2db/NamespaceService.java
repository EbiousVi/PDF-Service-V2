package com.example.pdf.service.h2db;

import com.example.pdf.domain.entity.Filename;
import com.example.pdf.domain.entity.Namespace;
import com.example.pdf.exception.CustomDBException;
import com.example.pdf.repository.NamespaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NamespaceService {
    private final NamespaceRepository namespaceRepository;

    @Autowired
    public NamespaceService(NamespaceRepository namespaceRepository) {
        this.namespaceRepository = namespaceRepository;
    }

    public Namespace getNamespaceByName(String name) throws CustomDBException {
        return namespaceRepository.findByName(name)
                .orElseThrow(() -> new CustomDBException("Namespace <" + name + "> not found!"));
    }

    public List<Namespace> getAllNamespace() {
        return namespaceRepository.findAll();
    }

    public List<String> getFilenamesByNamespace(String name) throws CustomDBException {
        Namespace namespace = this.getNamespaceByName(name);
        return namespace.getFilenames()
                .stream()
                .map(Filename::getName)
                .collect(Collectors.toList());
    }

    public void addNamespace(String name) throws CustomDBException {
        Namespace namespace = new Namespace(name);
        if (!namespaceRepository.existsByName(name)) {
            namespaceRepository.save(namespace);
        } else {
            throw new CustomDBException("Namespace <" + name + "> already exist!");
        }
    }

    public void deleteNamespaceByName(String name) throws CustomDBException {
        Namespace namespace = this.getNamespaceByName(name);
        namespaceRepository.delete(namespace);
    }

    public void deleteAll() {
        namespaceRepository.deleteAll();
    }
}
