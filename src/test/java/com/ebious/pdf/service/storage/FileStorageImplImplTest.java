package com.ebious.pdf.service.storage;

import com.ebious.pdf.domain.enums.DirName;
import com.ebious.pdf.exception.StorageException;
import com.ebious.pdf.service.storage.registry.proxy.RegistryProxyCache;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class FileStorageImplImplTest {

    private static final Path FILE_STORAGE_DIR = Paths.get("src", "test", "service-storage", "file-storage");
    private static final Path UPLOAD_DIR = FILE_STORAGE_DIR.resolve(DirName.UPLOAD.name);
    private static final Path LOAD_AS_RES_BY_PATH_DIR = FILE_STORAGE_DIR.resolve("load-as-res-by-path");
    private static final String REGISTRY_ID = "REGISTRY_ID";

    @Mock
    RegistryProxyCache registryProxyCache;

    @InjectMocks
    FileStorageImpl fileStorage;

    @BeforeAll
    static void init() throws IOException {
        FileSystemUtils.deleteRecursively(FILE_STORAGE_DIR);
        Files.createDirectories(FILE_STORAGE_DIR);
        Files.createDirectory(UPLOAD_DIR);
        Files.createDirectory(LOAD_AS_RES_BY_PATH_DIR);
    }

    @BeforeEach
    void setUp() {
        //FieldSetter.setField(storage, storage.getClass().getDeclaredField("storage"), testStorage);
        ReflectionTestUtils.setField(fileStorage, "storage", FILE_STORAGE_DIR);
    }

    @Test
    void initStorage() {
        fileStorage.initStorage(FILE_STORAGE_DIR);
        Assertions.assertThat(FILE_STORAGE_DIR).exists();
    }

    @Test
    void loadAsResourceByPath() throws IOException {
        Path file = Files.createFile(LOAD_AS_RES_BY_PATH_DIR.resolve("loadByPath.txt"));
        Resource resource = fileStorage.findResource(file);
        Assertions.assertThat(resource.exists()).isTrue();
    }

    @Test
    void loadAsResourceByPathFailed() {
        Assertions.assertThatThrownBy(() -> fileStorage.findResource(Paths.get("invalid")))
                .isInstanceOf(StorageException.class);
    }

    @Test
    void loadAsResourceByFilename() throws StorageException, IOException {
        Path loadAsResourceByFilename = Files.createDirectory(UPLOAD_DIR.resolve("load-as-res-by-filename"));
        Path file = Files.createFile(loadAsResourceByFilename.resolve("loadByFilename.txt"));
        Resource resourceLoadByFilename = fileStorage.loadAsResource(file.getFileName().toString());
        Assertions.assertThat(resourceLoadByFilename.exists()).isTrue();
    }

    @Test
    void saveUploadedFile() throws StorageException {
        MockMultipartFile multipartFile = new MockMultipartFile("file",
                "singleFile.txt", MediaType.TEXT_PLAIN_VALUE, "test".getBytes());
        doReturn(REGISTRY_ID).when(registryProxyCache).addEntry(Mockito.anyString());
        doReturn("").when(registryProxyCache).getEntry(REGISTRY_ID);

        String registryId = fileStorage.saveFile(multipartFile);

        boolean isSaved = Files.exists(UPLOAD_DIR.resolve(multipartFile.getOriginalFilename()));
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(registryId).isEqualTo(REGISTRY_ID);
        softAssertions.assertThat(isSaved).isTrue();
        softAssertions.assertAll();
        verify(registryProxyCache).addEntry(Mockito.anyString());
        verify(registryProxyCache).getEntry(REGISTRY_ID);
    }

    @Test
    void saveUploadedFiles() throws StorageException {
        MultipartFile file1 = new MockMultipartFile("file",
                "file1.txt", MediaType.TEXT_PLAIN_VALUE, "test1".getBytes());
        MultipartFile file2 = new MockMultipartFile("file",
                "file2.txt", MediaType.TEXT_PLAIN_VALUE, "test2".getBytes());
        MultipartFile file3 = new MockMultipartFile("file",
                "file3.txt", MediaType.TEXT_PLAIN_VALUE, "test3".getBytes());
        List<MultipartFile> files = Arrays.asList(file1, file2, file3);
        doReturn(REGISTRY_ID).when(registryProxyCache).addEntry(Mockito.anyString());
        doReturn("").when(registryProxyCache).getEntry(REGISTRY_ID);

        String registryId = fileStorage.saveFiles(files);

        assertSoftly(
                softAssertions -> {
                    softAssertions.assertThat(registryId).isEqualTo(REGISTRY_ID);
                    for (MultipartFile file : files) {
                        boolean isSaved = Files.exists(UPLOAD_DIR.resolve(file.getOriginalFilename()));
                        softAssertions.assertThat(isSaved).isTrue();
                    }
                }
        );
    }
}