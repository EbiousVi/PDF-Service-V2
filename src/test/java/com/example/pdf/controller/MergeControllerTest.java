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
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MergeControllerTest {

    @Autowired
    MergeController mergeController;

    @TempDir
    static Path testStorage;
    static Path testPdf1;
    static Path testPdf2;

    @BeforeAll
    static void createTestPdfFiles() {
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
            testPdf1 = testStorage.resolve("test1.pdf");
            document.save(testPdf1.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (PDDocument document = new PDDocument()) {
            for (int pageContent = 10; pageContent < 50; pageContent += 10) {
                PDPage page = new PDPage();
                document.addPage(page);
                PDPageContentStream contentStream = new PDPageContentStream(document, page);
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 192);
                contentStream.beginText();
                contentStream.showText(String.valueOf(pageContent));
                contentStream.endText();
                contentStream.close();
            }
            testPdf2 = testStorage.resolve("test2.pdf");
            document.save(testPdf2.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void uploadFiles() throws IOException, PdfServiceException, StorageException {
        MultipartFile mockMultipartFile1 = new MockMultipartFile(
                "file", testPdf1.getFileName().toString(),
                MediaType.APPLICATION_PDF_VALUE, Files.readAllBytes(testPdf1));
        MultipartFile mockMultipartFile2 = new MockMultipartFile(
                "file", testPdf2.getFileName().toString(),
                MediaType.APPLICATION_PDF_VALUE, Files.readAllBytes(testPdf2));

        List<String> list = mergeController.uploadFiles(Arrays.asList(mockMultipartFile1, mockMultipartFile2));
        assertFalse(list.isEmpty());

        Integer[] order = {0, 1};
        ResponseEntity<Resource> mergedFile = mergeController.merge(order);
        assertTrue(mergedFile.getBody().getFile().exists());
    }
}