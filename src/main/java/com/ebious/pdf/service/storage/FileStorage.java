package com.ebious.pdf.service.storage;

import com.ebious.pdf.domain.enums.DirName;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;

public interface FileStorage {

    void initStorage(Path path);

    void clearStorage(Path path);

    String saveFile(MultipartFile file);

    String saveFiles(List<MultipartFile> files);

    Resource loadAsResource(String filename);

    Resource findResource(Path path);

    Path getStorageSpace(String registryId, DirName dirName);

    Path getStorageFile(String registryId, DirName dirName);

    List<Path> getStorageFiles(String registryId, DirName dirName);
}
