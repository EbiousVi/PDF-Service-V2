package com.ebious.pdf.service.naming;

import com.ebious.pdf.domain.entity.Namespace;
import com.ebious.pdf.service.naming.dao.FilenameDao;
import com.ebious.pdf.service.naming.dao.FilenameDaoImpl;
import com.ebious.pdf.service.naming.dao.NamespaceDao;
import com.ebious.pdf.service.naming.exporter.FileExporter;
import com.ebious.pdf.service.naming.importer.FileImporter;
import com.ebious.pdf.exception.NamingServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NamingServiceImpl implements NamingService {

    private final static Logger logger = LoggerFactory.getLogger(NamingServiceImpl.class);
    private final NamespaceDao namespaceDao;
    private final FilenameDao filenameDao;
    private final FileImporter<Namespace> jsonImporter;
    private final FileExporter<Namespace> jsonExporter;

    @Autowired
    public NamingServiceImpl(NamespaceDao namespaceDao, FilenameDaoImpl filenameDao,
                             FileImporter<Namespace> jsonImporter,
                             FileExporter<Namespace> jsonExporter) {
        this.namespaceDao = namespaceDao;
        this.filenameDao = filenameDao;
        this.jsonImporter = jsonImporter;
        this.jsonExporter = jsonExporter;
    }

    @Override
    public Map<String, List<String>> collectNamespacesAndFilenames() {
        return namespaceDao.findAll().stream()
                .collect(Collectors.toMap(Namespace::getName, Namespace::getFilenames));
    }

    @Override
    public Boolean addFilenameToNamespace(String namespace, String filename) throws NamingServiceException {
        Optional<Namespace> byName = namespaceDao.findByName(namespace);
        if (byName.isPresent()) {
            return filenameDao.save(namespace, filename);
        } else {
            throw new NamingServiceException("Namespace = " + namespace + " not found!");
        }
    }

    @Override
    public Boolean addNamespace(String namespace) throws NamingServiceException {
        Optional<Namespace> byName = namespaceDao.findByName(namespace);
        if (!byName.isPresent()) {
            return namespaceDao.save(namespace);
        } else {
            throw new NamingServiceException("Namespace = " + namespace + " already exists");
        }
    }

    @Override
    public List<String> getAllNamespaceNames() {
        return namespaceDao.findAll().stream()
                .map(Namespace::getName)
                .collect(Collectors.toList());
    }

    @Override
    public Boolean deleteNamespace(String namespace) {
        return namespaceDao.delete(namespace);
    }

    @Override
    public Boolean deleteFilename(String filename, String namespace) {
        return filenameDao.delete(filename, namespace);
    }

    @Override
    public Path exportToFile() {
        return jsonExporter.export(namespaceDao.findAll());
    }

    @Override
    @Transactional
    public void importFromFile(MultipartFile file) {
        List<Namespace> importNamespaces = deleteDuplicates(jsonImporter.importFromFile(file));
        for (Namespace namespace : importNamespaces) {
            namespaceDao.save(namespace.getName());
            filenameDao.saveAll(namespace.getName(), namespace.getFilenames());
        }
    }

    /**
     * If namespace already exists, then it will be ignored
     */
    private List<Namespace> deleteDuplicates(List<Namespace> namespaces) {
        List<String> existsNamespaces = getAllNamespaceNames();
        ArrayDeque<Namespace> arrayDeque = new ArrayDeque<>(namespaces);
        for (Namespace namespace : arrayDeque) {
            if (existsNamespaces.contains(namespace.getName())) {
                arrayDeque.pop();
            } else {
                List<String> filenames = namespace.getFilenames().stream().distinct().collect(Collectors.toList());
                namespace.setFilenames(filenames);
            }
        }
        return new ArrayList<>(arrayDeque);
    }
}
