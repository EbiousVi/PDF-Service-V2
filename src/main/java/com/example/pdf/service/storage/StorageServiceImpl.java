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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(pathManager.getStorage().toFile());
    }

    @Override
    public void init() throws StorageException {
        try {
            Files.createDirectories(pathManager.getStorage());
        } catch (IOException e) {
            throw new StorageException("Can't create storage!");
        }
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
        if (file.isEmpty()) throw new StorageException("File not present");
        DirUtils.createStorageToUploadedFiles(serviceType);
        try (InputStream inputStream = file.getInputStream()) {
            Path uploadedFile = pathManager.getDirAtRootByName(DirsAtRoot.UPLOADED).resolve(Objects.requireNonNull(file.getOriginalFilename()));
            Files.copy(inputStream, uploadedFile, StandardCopyOption.REPLACE_EXISTING);
            pathManager.setUploadedFile(uploadedFile);
            return uploadedFile;
        } catch (IOException e) {
            throw new StorageException("Can't store uploaded file");
        }
    }

    public List<Path> saveUploadedFiles(MultipartFile[] files, ServiceType serviceType) throws StorageException {
        if (files.length == 0) throw new StorageException("File not present");
        DirUtils.createStorageToUploadedFiles(serviceType);
        Path uploadedDir = pathManager.getDirAtRootByName(DirsAtRoot.UPLOADED);
        List<Path> uploadedFilesPaths = new ArrayList<>();
        //It necessary to merge files.
        //Because is simple way not requiring freq access to the file system;
        Map<Integer, Path> uploadedFiles = new HashMap<>();
        for (int ordinal = 0; ordinal < files.length; ordinal++) {
            try (InputStream inputStream = files[ordinal].getInputStream()) {
                Path uploadedFile = uploadedDir.resolve(Objects.requireNonNull(files[ordinal].getOriginalFilename()));
                Files.copy(inputStream, uploadedFile, StandardCopyOption.REPLACE_EXISTING);
                uploadedFilesPaths.add(uploadedFile);
                uploadedFiles.put(ordinal, uploadedFile);
            } catch (IOException e) {
                throw new StorageException("Can't store uploaded files");
            }
        }
        pathManager.setUploadedFiles(uploadedFiles);
        return uploadedFilesPaths;
    }
}
