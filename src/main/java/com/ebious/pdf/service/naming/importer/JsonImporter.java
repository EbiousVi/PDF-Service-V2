package com.ebious.pdf.service.naming.importer;

import com.ebious.pdf.domain.dto.NamespaceDto;
import com.ebious.pdf.domain.entity.Namespace;
import com.ebious.pdf.exception.NamingServiceException;
import com.ebious.pdf.service.naming.mapper.NamespaceMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JsonImporter implements FileImporter<Namespace> {

    private final static Logger logger = LoggerFactory.getLogger(JsonImporter.class);
    private final ObjectMapper objectMapper;
    private final NamespaceMapper namespaceMapper;

    public JsonImporter(ObjectMapper objectMapper, NamespaceMapper namespaceMapper) {
        this.objectMapper = objectMapper;
        this.namespaceMapper = namespaceMapper;
    }

    @Override
    public List<Namespace> importFromFile(MultipartFile file) {
        return loadFromJson(file).stream()
                .map(namespaceMapper::map)
                .collect(Collectors.toList());
    }

    private List<NamespaceDto> loadFromJson(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            return objectMapper.readValue(inputStream, new TypeReference<List<NamespaceDto>>() {
            });
        } catch (IOException e) {
            logger.warn("Can not import from file!", e);
            throw new NamingServiceException("Can't read form file = " + file.getOriginalFilename());
        }
    }
}
