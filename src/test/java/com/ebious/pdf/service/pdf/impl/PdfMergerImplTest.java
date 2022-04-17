package com.ebious.pdf.service.pdf.impl;

import com.ebious.pdf.TestPDFHelper;
import com.ebious.pdf.domain.enums.DirName;
import com.ebious.pdf.service.storage.FileStorage;
import com.ebious.pdf.service.storage.FileStorageImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class PdfMergerImplTest {

    private static final Path MERGE_SERVICE_DIR = Paths.get("src", "test", "service-storage", "pdf-storage", "merge-service");
    private static final Path UPLOAD_DIR = MERGE_SERVICE_DIR.resolve(DirName.UPLOAD.name);
    private static final Path MERGE_DIR = MERGE_SERVICE_DIR.resolve(DirName.MERGE.name);
    private static final List<String> UPLOADED_FILE_1_PAGES_VALUE = Arrays.asList("0", "1", "2", "3", "4");
    private static final List<String> UPLOADED_FILE_2_PAGES_VALUE = Arrays.asList("0", "10", "20", "30", "40");
    private static final List<String> UPLOADED_FILE_3_PAGES_VALUE = Arrays.asList("0", "100", "200", "300", "400");
    private static final String REGISTRY_ID = "REGISTRY_ID";
    private static Path uploadedFile1;
    private static Path uploadedFile2;
    private static Path uploadedFile3;

    @Mock
    FileStorage fileStorage;

    @InjectMocks
    PdfMergerImpl pdfMerger;

    @BeforeAll
    public static void createTestPDF() throws IOException {
        FileSystemUtils.deleteRecursively(MERGE_SERVICE_DIR);
        Files.createDirectories(MERGE_SERVICE_DIR);
        Files.createDirectory(UPLOAD_DIR);
        Files.createDirectory(MERGE_DIR);
        uploadedFile1 = TestPDFHelper.createTestPDF(UPLOAD_DIR, "test-upload-1", 1);
        uploadedFile2 = TestPDFHelper.createTestPDF(UPLOAD_DIR, "test-upload-2", 10);
        uploadedFile3 = TestPDFHelper.createTestPDF(UPLOAD_DIR, "test-upload-3", 100);
    }

    @Test
    void merge() {
        doReturn(UPLOAD_DIR).when(fileStorage).getStorageSpace(REGISTRY_ID, DirName.UPLOAD);
        doReturn(MERGE_DIR).when(fileStorage).getStorageSpace(REGISTRY_ID, DirName.MERGE);
        List<String> expected = Stream.of(UPLOADED_FILE_2_PAGES_VALUE, UPLOADED_FILE_1_PAGES_VALUE, UPLOADED_FILE_3_PAGES_VALUE)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        List<String> filenames = Arrays.asList(
                uploadedFile2.getFileName().toString(),
                uploadedFile1.getFileName().toString(),
                uploadedFile3.getFileName().toString());

        Path mergedFile = pdfMerger.merge(REGISTRY_ID, filenames);

        List<String> actual = TestPDFHelper.readPDFPagesValue(mergedFile);
        assertThat(expected).isEqualTo(actual);

        Mockito.verify(fileStorage).getStorageSpace(REGISTRY_ID, DirName.UPLOAD);
        Mockito.verify(fileStorage).getStorageSpace(REGISTRY_ID, DirName.MERGE);
    }
}
