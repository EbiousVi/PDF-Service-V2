package com.ebious.pdf.service.uploader;

import com.ebious.pdf.controller.resource.ResourceController;
import com.ebious.pdf.domain.dto.UploadedDto;
import com.ebious.pdf.domain.enums.DirName;
import com.ebious.pdf.service.storage.FileStorageImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UploadedDtoService {

    private final FileStorageImpl fileStorage;

    @Autowired
    public UploadedDtoService(FileStorageImpl fileStorage) {
        this.fileStorage = fileStorage;
    }

    public UploadedDto prepareUploadedDto(String registryId, List<Path> renders) {
        List<String> rendersUri = mapImagesPathToURI(renders);
        List<String> filenames = fileStorage.getStorageFiles(registryId, DirName.UPLOAD).stream()
                .map(path -> path.getFileName().toString())
                .collect(Collectors.toList());
        return new UploadedDto(registryId, rendersUri, filenames);
    }

    public List<String> mapImagesPathToURI(List<Path> list) {
        return list.stream()
                .map(path -> MvcUriComponentsBuilder.fromMethodName(
                                ResourceController.class, "serveFile", path.getFileName().toString())
                        .build()
                        .toUri()
                        .toString())
                .collect(Collectors.toList());
    }
}
