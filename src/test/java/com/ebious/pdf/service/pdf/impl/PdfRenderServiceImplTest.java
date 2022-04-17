package com.ebious.pdf.service.pdf.impl;

import com.ebious.pdf.TestPDFHelper;
import com.ebious.pdf.domain.enums.DirName;
import com.ebious.pdf.service.storage.FileStorage;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PdfRenderServiceImplTest {

    private static final Path RENDER_SERVICE_DIR = Paths.get("src", "test", "service-storage", "pdf-storage", "render-service");
    private static final Path UPLOAD_DIR = RENDER_SERVICE_DIR.resolve(DirName.UPLOAD.name);
    private static final Path RENDER_DIR = RENDER_SERVICE_DIR.resolve(DirName.RENDER.name);
    private static final String REGISTRY_ID = "REGISTRY_ID";
    private static Path uploadedFile1;
    private static Path uploadedFile2;

    @Mock
    FileStorage fileStorage;

    @InjectMocks
    RenderPdfToJpegImpl renderPdfToJpeg;

    @BeforeAll
    public static void init() throws IOException {
        FileSystemUtils.deleteRecursively(RENDER_SERVICE_DIR);
        Files.createDirectories(RENDER_SERVICE_DIR);
        Files.createDirectory(UPLOAD_DIR);
        Files.createDirectory(RENDER_DIR);
        uploadedFile1 = TestPDFHelper.createTestPDF(UPLOAD_DIR, "test-upload-1", 1);
        uploadedFile2 = TestPDFHelper.createTestPDF(UPLOAD_DIR, "test-upload-2", 10);
    }

    @Test
    void renderAllPages() {
        doReturn(uploadedFile1).when(fileStorage).getStorageFile(REGISTRY_ID, DirName.UPLOAD);
        doReturn(RENDER_DIR).when(fileStorage).getStorageSpace(REGISTRY_ID, DirName.RENDER);

        List<Path> pageCovers = renderPdfToJpeg.renderPageCovers(REGISTRY_ID);

        assertThat(pageCovers).allMatch(Files::exists);
        verify(fileStorage).getStorageFile(REGISTRY_ID, DirName.UPLOAD);
        verify(fileStorage).getStorageSpace(REGISTRY_ID, DirName.RENDER);
    }

    @Test
    void renderCover() {
        doReturn(Arrays.asList(uploadedFile1, uploadedFile2)).when(fileStorage).getStorageFiles(REGISTRY_ID, DirName.UPLOAD);
        doReturn(RENDER_DIR).when(fileStorage).getStorageSpace(REGISTRY_ID, DirName.RENDER);

        List<Path> fileCover = renderPdfToJpeg.renderFileCovers(REGISTRY_ID);

        assertThat(fileCover).allMatch(Files::exists);
        verify(fileStorage).getStorageFiles(REGISTRY_ID, DirName.UPLOAD);
        verify(fileStorage).getStorageSpace(REGISTRY_ID, DirName.RENDER);
    }
}