package com.example.pdf.service.h2db;

import com.example.pdf.domain.entity.Namespace;
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

    public Namespace getNamespaceByName(String name) {
        return namespaceRepository.findByNamespace(name);
    }

    public List<String> getAllNamespace() {
        return namespaceRepository.findAll()
                .stream()
                .map(Namespace::getNamespace)
                .collect(Collectors.toList());
    }

    public void addNamespace(String name) {
        Namespace namespace = new Namespace();
        namespace.setNamespace(name);
        namespaceRepository.save(namespace);
    }

    public void deleteNamespaceByName(String name) {
        Namespace namespace = this.getNamespaceByName(name);
        namespaceRepository.delete(namespace);
    }

    public void deleteAll() {
        namespaceRepository.deleteAll();
    }
}
