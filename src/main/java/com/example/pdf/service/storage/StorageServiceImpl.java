package com.example.pdf.service.storage;

import com.example.pdf.domain.enums.DirsAtRoot;
import com.example.pdf.domain.enums.ServiceType;
import com.example.pdf.exception.StorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

@Service
public class StorageServiceImpl implements StorageService {
    private final PathManager pathManager;

    @Autowired
    public StorageServiceImpl(PathManager pathManager) {
        this.pathManager = pathManager;
    }

    @Override
    public void initStorage() throws StorageException {
        try {
            Files.createDirectories(pathManager.getStorage());
        } catch (IOException e) {
            e.printStackTrace();
            throw new StorageException("Can't create storage!");
        }
    }

    @Override
    public void deleteStorage() {
        FileSystemUtils.deleteRecursively(pathManager.getStorage().toFile());
    }

    @Override
    public Resource loadAsResource(String filename) throws StorageException {
        try (Stream<Path> walk = Files.walk(pathManager.getUploadedRootDir())) {
            Optional<Path> path = walk
                    .filter(Files::isRegularFile)
                    .filter(file -> file.getFileName().toString().equals(filename))
                    .map(Path::toAbsolutePath)
                    .findFirst();
            Resource resource = new UrlResource(path.orElseThrow(() -> new StorageException("Resource Not Found")).toUri());
            if (resource.exists()) {
                return resource;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new StorageException("File not found " + filename);
    }

    @Override
    public Resource loadAsResource(Path path) throws StorageException {
        try {
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists()) {
                return resource;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new StorageException("File not found " + path.getFileName());
    }

    @Override
    public Path saveUploadedFile(MultipartFile file, ServiceType serviceType) throws StorageException {
        if (file == null || file.isEmpty()) throw new StorageException("File not present");
        this.createStorageToUploadedFile(serviceType);
        try (InputStream inputStream = file.getInputStream()) {
            String filename = Objects.requireNonNull(file.getOriginalFilename());
            Path uploadedFile = pathManager.getDirAtRootByName(DirsAtRoot.UPLOAD).resolve(filename);
            Files.copy(inputStream, uploadedFile, StandardCopyOption.REPLACE_EXISTING);
            pathManager.setUploadedFile(uploadedFile);
            return uploadedFile;
        } catch (IOException e) {
            e.printStackTrace();
            throw new StorageException("Can't store uploaded file = " + file.getOriginalFilename());
        }
    }

    @Override
    public List<Path> saveUploadedFiles(List<MultipartFile> files, ServiceType serviceType) throws StorageException {
        if (files == null || files.isEmpty()) throw new StorageException("File not present");
        this.createStorageToUploadedFile(serviceType);
        Path uploadedDir = pathManager.getDirAtRootByName(DirsAtRoot.UPLOAD);
        List<Path> uploadedFiles = new ArrayList<>();
        for (int ordinal = 0; ordinal < files.size(); ordinal++) {
            try (InputStream inputStream = files.get(ordinal).getInputStream()) {
                String filename = files.get(ordinal).getOriginalFilename();
                Path uploadedFile = uploadedDir.resolve(filename);
                Files.copy(inputStream, uploadedFile, StandardCopyOption.REPLACE_EXISTING);
                uploadedFiles.add(uploadedFile);
                pathManager.putUploadedFileToUploadedFiles(ordinal, uploadedFile);
            } catch (IOException e) {
                e.printStackTrace();
                throw new StorageException("Can't store uploaded files = " + files.get(ordinal).getOriginalFilename());
            }
        }
        return uploadedFiles;
    }

    private void createStorageToUploadedFile(ServiceType serviceType) throws StorageException {
        Path uploadedRootDir = pathManager.getStorage().resolve(genRootDirName(serviceType));
        try {
            Files.createDirectory(uploadedRootDir);
            pathManager.setUploadedRootDir(uploadedRootDir);
        } catch (IOException e) {
            e.printStackTrace();
            throw new StorageException("Can't create root directory");
        }
        Map<DirsAtRoot, Path> dirStructureByType = this.createDirStructureByType(serviceType);
        pathManager.setDirsAtRoot(dirStructureByType);
    }

    private Path createDirAtRoot(String dirName) throws StorageException {
        try {
            Path dirPath = pathManager.getUploadedRootDir().resolve(dirName);
            if (!Files.exists(dirPath)) {
                Files.createDirectory(dirPath);
            }
            return dirPath;
        } catch (IOException e) {
            e.printStackTrace();
            throw new StorageException("Can't create directory at root");
        }
    }

    private Map<DirsAtRoot, Path> createDirStructureByType(ServiceType serviceType) throws StorageException {
        Map<DirsAtRoot, Path> dirsAtRootMap = new HashMap<>();
        for (DirsAtRoot dirsAtRoot : serviceType.getDirAtRoot()) {
            Path dirAtRootPath = createDirAtRoot(dirsAtRoot.name().toLowerCase());
            dirsAtRootMap.put(dirsAtRoot, dirAtRootPath);
        }
        return dirsAtRootMap;
    }

    private String genRootDirName(ServiceType serviceType) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now) + "_" + serviceType.name() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
}
