package com.example.pdf.service.storage;

import com.example.pdf.domain.enums.DirsAtRoot;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Component
public final class PathManager {
    private final Path storage = Paths.get("src", "main", "resources", "pdf-service");
    private Path uploadedRootDir;
    private Path uploadedFile;
    private Map<Integer, Path> uploadedFiles;
    private Map<DirsAtRoot, Path> dirsAtRoot;

    public Path getStorage() {
        return storage;
    }

    public Path getUploadedRootDir() {
        return uploadedRootDir;
    }

    public void setUploadedRootDir(Path uploadedRootDir) {
        this.uploadedRootDir = uploadedRootDir;
    }

    public Path getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(Path uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public void setUploadedFiles(Map<Integer, Path> uploadedFiles) {
        this.uploadedFiles = uploadedFiles;
    }

    public Map<Integer, Path> getUploadedFiles() {
        return uploadedFiles;
    }

    public void setDirsAtRoot(Map<DirsAtRoot, Path> dirsAtRoot) {
        this.dirsAtRoot = dirsAtRoot;
    }

    public Path getDirAtRootByName(DirsAtRoot dirsAtroot) {
        return dirsAtRoot.get(dirsAtroot);
    }
}
