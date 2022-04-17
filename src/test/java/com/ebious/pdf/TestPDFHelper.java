package com.ebious.pdf;

import com.ebious.pdf.domain.enums.Extension;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestPDFHelper {

    private static final int PAGE_LIMIT = 5;

    public static Path createTestPDF(Path uploadDir, String filename, int k) {
        try (PDDocument document = new PDDocument()) {
            Path path = uploadDir.resolve(filename + Extension.PDF.name);
            for (int pageContent = 0; pageContent < PAGE_LIMIT * k; pageContent = pageContent + k) {
                PDPage page = new PDPage();
                document.addPage(page);
                PDPageContentStream contentStream = new PDPageContentStream(document, page);
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 255);
                contentStream.beginText();
                contentStream.showText(String.valueOf(pageContent));
                contentStream.endText();
                contentStream.close();
            }
            document.save(path.toFile());
            return path;
        } catch (IOException e) {
            throw new RuntimeException("Can't create test PDF file, TEST FAILED!");
        }
    }

    public static List<String> readPDFPagesValue(Path file) {
        List<String> actualPagesContent = new ArrayList<>();
        try (PDDocument document = PDDocument.load(file.toFile())) {
            PDFTextStripper tStripper = new PDFTextStripper();
            String fileContent = tStripper.getText(document);
            String[] pagesContent = fileContent.split("\\r?\\n");
            actualPagesContent.addAll(Arrays.asList(pagesContent));
            return actualPagesContent;
        } catch (IOException e) {
            throw new RuntimeException("Test failed!", e);
        }
    }
}
