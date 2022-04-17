package com.ebious.pdf.service.storage;

import com.ebious.pdf.domain.enums.DirName;
import com.ebious.pdf.exception.StorageException;
import com.ebious.pdf.service.storage.registry.proxy.RegistryProxyCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * When User upload file/s, storage create Root upload directory {@link #createRootUploadDir()} to uploaded file/s;
 * In response, User receives the registry ID is a UUID for subsequent access to uploaded files;
 * Root upload directory containing directories for further interactions with the uploaded file/s;
 * Uploaded file/s is recommended to be stored in a {@link DirName#UPLOAD} directory;
 * Root upload directory has a unique name;
 * Root upload directory has the maximum number of directory levels = 1;
 * The storage has a cache for quick access by registry ID;
 * Cache contains key = registry id, value = root upload directory name;
 * Default cache size = 128, expire after write = 30 min;
 **/
@Service
public class FileStorageImpl implements FileStorage {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageImpl.class);
    private final Path storage = Paths.get("service-storage", "pdf-storage");
    private final RegistryProxyCache registryProxyCache;

    public FileStorageImpl(RegistryProxyCache registryProxyCache) {
        this.registryProxyCache = registryProxyCache;
    }

    /**
     * Init Storage during initialization
     */
    @PostConstruct
    public void postConstruct() {
        initStorage(storage);
    }

    /**
     * Storage will be cleared after completion
     */
    @PreDestroy
    public void preDestroy() {
        clearStorage(storage);
    }

    /**
     * Application will not be launched if it fails to create the storage
     */
    @Override
    public void initStorage(Path path) {
        try {
            if (!Files.exists(path)) Files.createDirectories(path);
        } catch (IOException e) {
            logger.error("Can not create storage during initialization", e);
            System.exit(-1);
        }
    }

    @Override
    public void clearStorage(Path path) {
        FileSystemUtils.deleteRecursively(path.toFile());
    }

    /**
     * Currently used only for loading image(page, file covers). All image has unique filename;
     * Use try catch with resources to release file descriptors
     **/
    @Override
    public Resource loadAsResource(String filename) {
        try (Stream<Path> walk = Files.walk(storage)) {
            Path path = walk
                    .filter(Files::isRegularFile)
                    .filter(file -> file.getFileName().toString().equals(filename))
                    .map(Path::toAbsolutePath)
                    .findFirst()
                    .orElseThrow(() -> new StorageException("File not found = " + filename));
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists()) return resource;
        } catch (IOException e) {
            logger.error("Can not walk storage", e);
        }
        throw new StorageException("Storage internal error! Try again or read logs!");
    }

    /**
     * Use try catch with resources to release file descriptors
     */
    @Override
    public Resource findResource(Path path) {
        try {
            Resource resource = new UrlResource(path.toUri());
            if (!resource.exists())
                throw new StorageException("Resource not found! Storage internal exception!");
            return resource;
        } catch (IOException e) {
            logger.error("Can not find resource", e);
            throw new StorageException("Resource not found! Storage internal exception!");
        }
    }

    /**
     * MultipartFile validated in the Controller, otherwise, validation is required
     */
    @Override
    public String saveFile(MultipartFile file) {
        String registryId = createRootUploadDir();
        try (InputStream inputStream = file.getInputStream()) {
            Path uploadedFile = getStorageSpace(registryId, DirName.UPLOAD).resolve(file.getOriginalFilename());
            Files.copy(inputStream, uploadedFile, StandardCopyOption.REPLACE_EXISTING);
            return registryId;
        } catch (IOException e) {
            logger.error("Can not store uploaded file", e);
            throw new StorageException("Can not store uploaded file = " + file.getOriginalFilename());
        }
    }

    /**
     * MultipartFile validated in the Controller, otherwise, validation is required
     */
    @Override
    public String saveFiles(List<MultipartFile> files) {
        String registryId = createRootUploadDir();
        for (MultipartFile file : files) {
            try (InputStream inputStream = file.getInputStream()) {
                Path uploadedFile = getStorageSpace(registryId, DirName.UPLOAD).resolve(file.getOriginalFilename());
                Files.copy(inputStream, uploadedFile, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                logger.error("Can not store uploaded files", e);
                throw new StorageException("Can not store upload file = " + file.getOriginalFilename());
            }
        }
        return registryId;
    }

    /**
     * Return storage space to file at root upload directory by registry id
     */
    @Override
    public Path getStorageSpace(String registryId, DirName dirName) {
        Path path = validateRegistryEntry(registryProxyCache.getEntry(registryId));
        return createServiceDir(path, dirName);
    }

    public Path getStorageFile(String registryId, DirName dirName) {
        Path storageSpace = getStorageSpace(registryId, dirName);
        try (Stream<Path> files = Files.list(storageSpace)) {
            return files
                    .findFirst()
                    .orElseThrow(() -> new StorageException("Can not access uploaded file!"));
        } catch (IOException e) {
            logger.error("Can not access uploaded file!", e);
            throw new StorageException("Can not access uploaded file!");
        }
    }

    @Override
    public List<Path> getStorageFiles(String registryId, DirName dirName) {
        Path storageSpace = getStorageSpace(registryId, dirName);
        try (Stream<Path> files = Files.list(storageSpace)) {
            return files.collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("Can not access uploaded file!", e);
            throw new StorageException("Can not access uploaded file!");
        }
    }

    private String createRootUploadDir() throws StorageException {
        try {
            String rootUploadDirName = generateRootUploadDirName();
            Files.createDirectory(storage.resolve(rootUploadDirName));
            return registryProxyCache.addEntry(rootUploadDirName);
        } catch (IOException e) {
            logger.error("Can not create Root upload directory", e);
            throw new StorageException("Can not create Root upload directory!");
        }
    }

    /**
     * Create directory for storing the results of the work of different services like split, merge, upload etc.
     */
    private Path createServiceDir(Path rootUploadDir, DirName dirName) {
        try {
            Path path = rootUploadDir.resolve(dirName.name);
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }
            return path;
        } catch (IOException e) {
            logger.error("Can't create service directory", e);
            throw new StorageException("Can't create service directory!");
        }
    }

    /**
     * Check if root upload dir is exists, before providing a path for interaction
     */
    private Path validateRegistryEntry(String rootUploadDirName) {
        Path rootUploadDir = storage.resolve(rootUploadDirName).normalize();
        if (Files.isDirectory(rootUploadDir)) {
            return rootUploadDir;
        } else {
            logger.error("Invalid Root upload directory at registry! Path: {}", rootUploadDir);
            throw new StorageException("Invalid Root upload directory!");
        }
    }

    /**
     * Root upload directory has unique name
     */
    private String generateRootUploadDirName() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yy_HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now) + "_" + UUID.randomUUID().toString().substring(0, 16);
    }
}

