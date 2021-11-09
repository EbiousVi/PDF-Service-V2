package com.example.pdf.service.pdf.impl;

import com.example.pdf.domain.enums.DirsAtRoot;
import com.example.pdf.exception.PdfServiceException;
import com.example.pdf.service.storage.PathManager;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipFile;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class SplitServiceImplTest {

    @InjectMocks
    SplitServiceImpl splitService;

    @Mock
    PathManager pathManager;

    @TempDir
    static Path testRootDir;
    static Path testUploadedFile;

    /**
     * Create Test PDF. Marked each page with a number. Page content equals page number.
     */
    @BeforeAll
    public static void createTestPDF() {
        try (PDDocument document = new PDDocument()) {
            for (int pageContent = 0; pageContent < 5; pageContent++) {
                PDPage page = new PDPage();
                document.addPage(page);
                PDPageContentStream contentStream = new PDPageContentStream(document, page);
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 72);
                contentStream.beginText();
                contentStream.showText(String.valueOf(pageContent));
                contentStream.endText();
                contentStream.close();
            }
            testUploadedFile = testRootDir.resolve("test.pdf");
            document.save(testUploadedFile.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    void setUploadedFile() {
        Mockito.when(pathManager.getUploadedFile()).thenReturn(testUploadedFile);
    }

    /**
     * If the selected pages are equal to the splitted pages value then the file is split correctly.
     * {@link SplitServiceImplTest#setUploadedFile()}
     */
    @Test
    void splitBySelectedPages() throws IOException, PdfServiceException {
        Mockito.when(pathManager.getDirAtRootByName(DirsAtRoot.SPLIT)).thenReturn(testRootDir);
        Integer[] selectedPages = {1, 2};
        Integer[] splittedPagesValue = new Integer[selectedPages.length];
        Path splittedPDF = splitService.splitBySelectedPages(selectedPages);
        try (PDDocument splitted = PDDocument.load(splittedPDF.toFile())) {
            PDFTextStripper tStripper = new PDFTextStripper();
            String pdfFileInText = tStripper.getText(splitted);
            String[] pagesValues = pdfFileInText.split("\\r?\\n");
            for (int i = 0; i < pagesValues.length; i++) {
                splittedPagesValue[i] = Integer.parseInt(pagesValues[i]);
            }
            assertArrayEquals(selectedPages, splittedPagesValue);
        }
    }

    /**
     * ImageIO.read == null, therefore image was not created.
     */
    @Test
    void renderingFilePages() throws PdfServiceException {
        Mockito.when(pathManager.getDirAtRootByName(DirsAtRoot.RENDER_IMG)).thenReturn(testRootDir);
        List<Path> renderImgs = splitService.renderFilePages(testUploadedFile);
        long createdImgCount = renderImgs.stream().filter(img -> {
            try {
                return ImageIO.read(new File(img.toString())) != null;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }).count();
        assertEquals(renderImgs.size(), createdImgCount);
    }

    /**
     * If PDDocument will be loaded, therefore it was created.
     */
    @Test
    void splitBySinglePages() throws PdfServiceException {
        Mockito.when(pathManager.getDirAtRootByName(DirsAtRoot.SPLIT_ALL)).thenReturn(testRootDir);
        List<Path> singlePages = splitService.splitBySinglePages();
        long createdSinglePageDocCount = singlePages.stream().filter(path -> {
            try (PDDocument document = PDDocument.load(path.toFile())) {
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }).count();
        assertEquals(singlePages.size(), createdSinglePageDocCount);
    }

    /**
     * If an exception is thrown, then this is not a zip file!
     */
    @Test
    void packToZip() throws PdfServiceException {
        Mockito.when(pathManager.getDirAtRootByName(DirsAtRoot.ZIP)).thenReturn(testRootDir);
        Path zipFile = splitService.packToZip(Collections.singletonList(testUploadedFile));
        assertDoesNotThrow(() -> {
            try (ZipFile zipfile = new ZipFile(zipFile.toFile())) {

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}