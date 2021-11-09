package com.example.pdf.service.storage;

import com.example.pdf.domain.enums.DirsAtRoot;
import com.example.pdf.domain.enums.ServiceType;
import com.example.pdf.exception.StorageException;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StorageServiceImplTest {

    @InjectMocks
    StorageServiceImpl storageService;

    @Mock
    PathManager pathManager;

    @TempDir
    static Path tempStorage;

    static Path testStorage = Paths.get("src", "test", "resources", "pdf-service", "test-storage");

    @Test
    @Order(0)
    void initStorage() throws StorageException {
        Mockito.when(pathManager.getStorage()).thenReturn(testStorage);
        storageService.initStorage();
        assertTrue(Files.exists(testStorage));
    }

    @Test
    @Order(1)
    void deleteStorage() {
        Mockito.when(pathManager.getStorage()).thenReturn(testStorage);
        storageService.deleteStorage();
        assertFalse(Files.exists(testStorage));
    }

    @Test
    @Order(2)
    void saveUploadedFile() throws StorageException {
        Mockito.when(pathManager.getStorage()).thenReturn(tempStorage);
        Mockito.when(pathManager.getUploadedRootDir()).thenReturn(tempStorage);
        Mockito.when(pathManager.getDirAtRootByName(DirsAtRoot.UPLOAD)).thenReturn(tempStorage);
        MockMultipartFile multipartFile = new MockMultipartFile("file",
                "test.txt", MediaType.TEXT_PLAIN_VALUE, "test".getBytes());
        Path uploadedFile = storageService.saveUploadedFile(multipartFile, ServiceType.TEST_CASE);
        assertTrue(Files.exists(uploadedFile));
    }

    @Test
    @Order(3)
    void saveUploadedFiles() throws StorageException {
        Mockito.when(pathManager.getDirAtRootByName(DirsAtRoot.UPLOAD)).thenReturn(tempStorage);
        Mockito.when(pathManager.getStorage()).thenReturn(tempStorage);
        Mockito.when(pathManager.getUploadedRootDir()).thenReturn(tempStorage);

        MultipartFile multipartFile1 = new MockMultipartFile("file",
                "test1.txt", MediaType.TEXT_PLAIN_VALUE, "test1".getBytes());
        MultipartFile multipartFile2 = new MockMultipartFile("file",
                "test2.txt", MediaType.TEXT_PLAIN_VALUE, "test2".getBytes());
        MultipartFile multipartFile3 = new MockMultipartFile("file",
                "test3.txt", MediaType.TEXT_PLAIN_VALUE, "test3".getBytes());

        List<MultipartFile> files = Arrays.asList(multipartFile1, multipartFile2, multipartFile3);

        List<Path> uploadedFile = storageService.saveUploadedFiles(files, ServiceType.TEST_CASE);
        for (Path path : uploadedFile) {
            assertTrue(Files.exists(path));
        }
    }

    @Test
    @Order(4)
    void loadAsResourceByPath() throws IOException, StorageException {
        Path file = Files.createFile(tempStorage.resolve("loadByPath.txt"));
        Resource resourceLoadByPath = storageService.loadAsResource(file);
        assertTrue(resourceLoadByPath.exists());
    }

    @Test
    @Order(5)
    void loadAsResourceByFilename() throws StorageException, IOException {
        Mockito.when(pathManager.getUploadedRootDir()).thenReturn(tempStorage);
        Path file = Files.createFile(tempStorage.resolve("loadByFilename.txt"));
        Resource resourceLoadByFilename = storageService.loadAsResource(file.getFileName().toString());
        assertTrue(resourceLoadByFilename.exists());
    }
}