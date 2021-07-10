package com.example.pdf.service.storage;

import com.example.pdf.domain.enums.DirsAtRoot;
import com.example.pdf.domain.enums.ServiceType;
import com.example.pdf.exception.StorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class DirUtils {
    private static PathManager pathManager;

    @Autowired
    DirUtils(PathManager pathManager) {
        DirUtils.pathManager = pathManager;
    }

    static Path createDirAtRoot(String dirName) throws StorageException {
        try {
            Path dirPath = pathManager.getUploadedRootDir().resolve(dirName);
            if (!Files.exists(dirPath)) {
                Files.createDirectory(dirPath);
            }
            return dirPath;
        } catch (IOException e) {
            throw new StorageException("Can't create directory at root", e);
        }
    }

    static void createStorageToUploadedFiles(ServiceType serviceType) throws StorageException {
        Path uploadedRootDir = pathManager.getStorage().resolve(genRootDirName(serviceType));
        try {
            Files.createDirectory(uploadedRootDir);
            pathManager.setUploadedRootDir(uploadedRootDir);
        } catch (IOException e) {
            throw new StorageException("Can't create root directory", e);
        }
        Map<DirsAtRoot, Path> dirsAtRootMap = new HashMap<>();
        for (DirsAtRoot dirsAtRoot : serviceType.getDirAtRoot()) {
            Path dirAtRootPath = createDirAtRoot(dirsAtRoot.name().toLowerCase());
            dirsAtRootMap.put(dirsAtRoot, dirAtRootPath);
        }
        pathManager.setDirsAtRoot(dirsAtRootMap);
    }

    private static String genRootDirName(ServiceType serviceType) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now) + "_" + serviceType.name() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
}
