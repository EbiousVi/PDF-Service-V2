package com.example.pdf.service.storage;

import com.example.pdf.domain.enums.DirsAtRoot;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
@Validated
public class PathManager {
    private final Path storage = Paths.get("src", "main", "resources", "pdf-service");
    private final Map<Integer, Path> uploadedFiles = new HashMap<>();
    private Path uploadedRootDir;
    private Path uploadedFile;
    private Map<DirsAtRoot, Path> dirsAtRoot;

    @NotNull(message = "{path_manager.null}")
    public Path getStorage() {
        return storage;
    }

    @NotNull(message = "{path_manager.null}")
    public Path getUploadedRootDir() {
        return uploadedRootDir;
    }

    @NotNull(message = "{path_manager.null}")
    public Path getUploadedFile() {
        return uploadedFile;
    }

    @NotNull(message = "{path_manager.null}")
    public Path getDirAtRootByName(DirsAtRoot dirsAtroot) {
        return dirsAtRoot.get(dirsAtroot);
    }

    @NotNull(message = "{path_manager.null}")
    public Path getUploadedFileByOrdinal(Integer ordinal) {
        return uploadedFiles.get(ordinal);
    }

    public void setUploadedRootDir(Path uploadedRootDir) {
        this.uploadedRootDir = uploadedRootDir;
    }

    public void setUploadedFile(Path uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public void setDirsAtRoot(Map<DirsAtRoot, Path> dirsAtRoot) {
        this.dirsAtRoot = dirsAtRoot;
    }

    public void putUploadedFileToUploadedFiles(Integer ordinal, Path path) {
        uploadedFiles.put(ordinal, path);
    }
}
