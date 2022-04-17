package com.ebious.pdf.service.pdf.impl;

import com.ebious.pdf.TestPDFHelper;
import com.ebious.pdf.domain.enums.DirName;
import com.ebious.pdf.service.storage.FileStorage;
import com.ebious.pdf.service.storage.FileStorageImpl;
import org.assertj.core.api.SoftAssertions;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PdfSplitterImplTest {

    private static final Path SPLIT_SERVICE_DIR = Paths.get("src", "test", "service-storage", "pdf-storage", "split-service");
    private static final Path UPLOAD_DIR = SPLIT_SERVICE_DIR.resolve(DirName.UPLOAD.name);
    private static final Path SPLIT_DIR = SPLIT_SERVICE_DIR.resolve(DirName.SPLIT.name);
    private static final Path SPLIT_ALL_DIR = SPLIT_SERVICE_DIR.resolve(DirName.SPLIT_ALL.name);
    private static final String REGISTRY_ID = "REGISTRY_ID";
    private static Path uploadedFile;

    @Mock
    FileStorage fileStorage;

    @InjectMocks
    PdfSplitterImpl pdfSplitter;

    @BeforeAll
    public static void init() throws IOException {
        FileSystemUtils.deleteRecursively(SPLIT_SERVICE_DIR);
        Files.createDirectories(SPLIT_SERVICE_DIR);
        Files.createDirectory(UPLOAD_DIR);
        Files.createDirectory(SPLIT_DIR);
        Files.createDirectory(SPLIT_ALL_DIR);
        uploadedFile = TestPDFHelper.createTestPDF(UPLOAD_DIR, "test-upload", 1);
    }

    @Test
    void splitBySelectedPages() {
        doReturn(uploadedFile).when(fileStorage).getStorageFile(REGISTRY_ID, DirName.UPLOAD);
        doReturn(SPLIT_DIR).when(fileStorage).getStorageSpace(REGISTRY_ID, DirName.SPLIT);

        List<Integer> selectedPages = Arrays.asList(1, 2);
        List<String> expected = Arrays.asList("1", "2");

        Path splittedFile = pdfSplitter.splitBySelectedPages(REGISTRY_ID, selectedPages);

        List<String> actual = TestPDFHelper.readPDFPagesValue(splittedFile);
        assertThat(expected).isEqualTo(actual);
        verify(fileStorage).getStorageFile(REGISTRY_ID, DirName.UPLOAD);
        verify(fileStorage).getStorageSpace(REGISTRY_ID, DirName.SPLIT);
    }

    @Test
    void splitBySinglePage() {
        doReturn(uploadedFile).when(fileStorage).getStorageFile(REGISTRY_ID, DirName.UPLOAD);
        doReturn(SPLIT_ALL_DIR).when(fileStorage).getStorageSpace(REGISTRY_ID, DirName.SPLIT_ALL);
        List<String> expected = Arrays.asList("0", "1", "2", "3", "4");
        List<String> actual = new ArrayList<>();

        List<Path> singlePages = pdfSplitter.splitBySinglePage(REGISTRY_ID);

        for (Path singlePage : singlePages) {
            actual.addAll(TestPDFHelper.readPDFPagesValue(singlePage));
        }

        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(singlePages.size()).isEqualTo(expected.size());
        softAssertions.assertThat(expected).isEqualTo(actual);
        softAssertions.assertAll();

        verify(fileStorage).getStorageFile(REGISTRY_ID, DirName.UPLOAD);
        verify(fileStorage).getStorageSpace(REGISTRY_ID, DirName.SPLIT_ALL);
    }
}
