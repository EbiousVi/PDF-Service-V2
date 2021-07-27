package com.example.pdf.controller;

import com.example.pdf.exception.PdfServiceException;
import com.example.pdf.exception.StorageException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SplitControllerTest {

    @Autowired
    SplitController splitController;

    @TempDir
    static Path testStorage;
    static Path testPdf;

    @BeforeAll
    static void createTestPdf() {
        try (PDDocument document = new PDDocument()) {
            for (int pageContent = 0; pageContent < 5; pageContent++) {
                PDPage page = new PDPage();
                document.addPage(page);
                PDPageContentStream contentStream = new PDPageContentStream(document, page);
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 192);
                contentStream.beginText();
                contentStream.showText(String.valueOf(pageContent));
                contentStream.endText();
                contentStream.close();
            }
            testPdf = testStorage.resolve("test.pdf");
            document.save(testPdf.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testController() throws PdfServiceException, StorageException, IOException {
        MultipartFile mockMultipartFile = new MockMultipartFile(
                "file", testPdf.getFileName().toString(),
                MediaType.APPLICATION_PDF_VALUE, Files.readAllBytes(testPdf));

        List<String> renderingImgs = splitController.uploadFile(mockMultipartFile);
        assertFalse(renderingImgs.isEmpty());

        Integer[] selectedPages = {1, 0};
        ResponseEntity<Resource> zip = splitController.splitBySinglePages();
        assertTrue(zip.getBody().getFile().exists());

        ResponseEntity<Resource> splittedPdf = splitController.splitBySelectedPages(selectedPages);
        assertTrue(splittedPdf.getBody().getFile().exists());
    }
}
