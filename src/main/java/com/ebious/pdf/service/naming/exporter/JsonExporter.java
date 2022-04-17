package com.ebious.pdf.service.naming.exporter;

import com.ebious.pdf.domain.entity.Namespace;
import com.ebious.pdf.domain.enums.Extension;
import com.ebious.pdf.exception.NamingServiceException;
import com.ebious.pdf.service.storage.FileStorage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class JsonExporter implements FileExporter<Namespace> {

    private static final String FILENAME = "export";
    private static final Path JSON_STORAGE = Paths.get("service-storage", "export", "json");
    private final static Logger logger = LoggerFactory.getLogger(JsonExporter.class);
    private final ObjectMapper objectMapper;
    private final FileStorage fileStorage;

    @Autowired
    public JsonExporter(ObjectMapper objectMapper, FileStorage fileStorage) {
        this.objectMapper = objectMapper;
        this.fileStorage = fileStorage;
    }

    @PostConstruct
    public void initJsonExportStorage() {
        fileStorage.initStorage(JSON_STORAGE);
    }

    @PreDestroy
    public void clearJsonExportStorage() {
        fileStorage.clearStorage(JSON_STORAGE);
    }

    @Override
    public Path export(List<Namespace> data) {
        Path json = JSON_STORAGE.resolve(FILENAME + Extension.JSON.name);
        try {
            objectMapper.writeValue(json.toFile(), data);
            return json;
        } catch (IOException e) {
            logger.error("Can not export database dump!", e);
            throw new NamingServiceException("Can not export database dump!");
        }
    }
}
