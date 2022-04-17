package com.ebious.pdf.service.zip;

import com.ebious.pdf.domain.enums.DirName;
import com.ebious.pdf.TestPDFHelper;
import com.ebious.pdf.service.storage.FileStorageImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ZipArchiverImplTest {

    private static final Path ZIP_SERVICE_DIR = Paths.get("src", "test", "service-storage", "pdf-storage", "zip-service");
    private static final String REGISTRY_ID = "REGISTRY_ID";
    private static Path file1ToZip;
    private static Path file2ToZip;

    @Mock
    FileStorageImpl storage;

    @InjectMocks
    ZipArchiverImpl zipArchiver;

    @BeforeAll
    static void init() throws IOException {
        FileSystemUtils.deleteRecursively(ZIP_SERVICE_DIR);
        Files.createDirectories(ZIP_SERVICE_DIR);
        file1ToZip = TestPDFHelper.createTestPDF(ZIP_SERVICE_DIR, "test-upload-1", 1);
        file2ToZip = TestPDFHelper.createTestPDF(ZIP_SERVICE_DIR, "test-upload-2", 10);
    }

    @Test
    void packToZip() {
        List<Path> filesToZip = Arrays.asList(file1ToZip, file2ToZip);
        doReturn(ZIP_SERVICE_DIR).when(storage).getStorageSpace(REGISTRY_ID, DirName.ZIP);
        doReturn(filesToZip).when(storage).getStorageFiles(REGISTRY_ID, DirName.ZIP);

        Path zipFile = zipArchiver.archive(REGISTRY_ID, DirName.ZIP);

        assertDoesNotThrow(() -> {
            //ZipException will be thrown, if this is not a Zip file
            try (ZipFile zipfile = new ZipFile(zipFile.toFile())) {
                Enumeration<? extends ZipEntry> entries = zipfile.entries();
                int i = 0;
                while (entries.hasMoreElements()) {
                    ZipEntry zipEntry = entries.nextElement();
                    String zipEntryFilename = zipEntry.getName();
                    String expectedZipFilename = filesToZip.get(i).getFileName().toString();
                    i++;
                    assertEquals(expectedZipFilename, zipEntryFilename);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        verify(storage).getStorageSpace(REGISTRY_ID, DirName.ZIP);
        verify(storage).getStorageFiles(REGISTRY_ID, DirName.ZIP);
    }
}
