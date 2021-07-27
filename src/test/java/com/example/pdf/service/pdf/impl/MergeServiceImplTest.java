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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class MergeServiceImplTest {

    @InjectMocks
    MergeServiceImpl mergeService;

    @Mock
    private PathManager pathManager;

    @TempDir
    static Path testStorage;// = Paths.get("src", "test", "resources", "pdf-service");
    static Path uploadedFile1;
    static Path uploadedFile2;
    static List<String> file1PagesValue = Arrays.asList("1", "2", "3");
    static List<String> file2PagesValue = Arrays.asList("100", "200", "300");


    /**
     * Create two test PDF. The first file is marked with values from {@link MergeServiceImplTest#file1PagesValue}.
     * The second file is marked with values from {@link MergeServiceImplTest#file2PagesValue}.
     */
    @BeforeAll
    public static void createTestPDF() {
        try (PDDocument document = new PDDocument()) {
            for (String pageContent : file1PagesValue) {
                PDPage page = new PDPage();
                document.addPage(page);
                PDPageContentStream contentStream = new PDPageContentStream(document, page);
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 72);
                contentStream.beginText();
                contentStream.showText(pageContent);
                contentStream.endText();
                contentStream.close();
            }
            uploadedFile1 = testStorage.resolve("test1.pdf");
            document.save(uploadedFile1.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (PDDocument document = new PDDocument()) {
            for (String pageContent : file2PagesValue) {
                PDPage page = new PDPage();
                document.addPage(page);
                PDPageContentStream contentStream = new PDPageContentStream(document, page);
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 72);
                contentStream.beginText();
                contentStream.showText(pageContent);
                contentStream.endText();
                contentStream.close();
            }
            uploadedFile2 = testStorage.resolve("test2.pdf");
            document.save(uploadedFile2.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ImageIO.read == null, therefore image was not created.
     */
    @Test
    void renderingFilesCover() throws PdfServiceException {
        Mockito.when(pathManager.getDirAtRootByName(DirsAtRoot.RENDER_IMG)).thenReturn(testStorage);
        List<Path> renderImgs = mergeService.renderFileCover(Arrays.asList(uploadedFile1, uploadedFile2));
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
     * Merge {@link MergeServiceImplTest#file1PagesValue} and
     * {@link MergeServiceImplTest#file2PagesValue} in a given order.
     * Parse pages value from merged PDF. And compare with expectedMergedFileValues;
     */
    @Test
    void merge() throws PdfServiceException, IOException {
        Mockito.when(pathManager.getDirAtRootByName(DirsAtRoot.MERGE)).thenReturn(testStorage);

        Integer[] order = {1, 2};
        Mockito.when(pathManager.getUploadedFileByOrdinal(order[0])).thenReturn(uploadedFile1);
        Mockito.when(pathManager.getUploadedFileByOrdinal(order[1])).thenReturn(uploadedFile2);

        List<String> expectedMergedFileValues = Stream.of(file1PagesValue, file2PagesValue)
                .flatMap(Collection::stream).collect(Collectors.toList());
        Path merge = mergeService.merge(order);

        List<String> mergeFileValues = new ArrayList<>();
        try (PDDocument splitted = PDDocument.load(merge.toFile())) {
            PDFTextStripper tStripper = new PDFTextStripper();
            String pdfFileInText = tStripper.getText(splitted);
            String[] pagesValues = pdfFileInText.split("\\r?\\n");
            mergeFileValues.addAll(Arrays.asList(pagesValues));
        }
        assertEquals(expectedMergedFileValues, mergeFileValues);
    }
}