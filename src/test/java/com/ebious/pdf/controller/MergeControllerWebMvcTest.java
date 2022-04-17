/*
package com.example.pdf.controller;

import com.example.pdf.domain.dto.MergeDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MergeController.class)
class MergeControllerWebMvcTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    MergeController mergeController;

    @TempDir
    static Path testStorage;
    static Path testPdf1;
    static Path testPdf2;
    static final String token = "MergeControllerWebMvcTest";
    static List<String> filenames;

    @BeforeAll
    static void createTestPdfFiles() {
        try (PDDocument document = new PDDocument()) {
            for (int i = 0; i < 5; i++) {
                PDPage page = new PDPage();
                document.addPage(page);
                PDPageContentStream contentStream = new PDPageContentStream(document, page);
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 192);
                contentStream.beginText();
                contentStream.showText(String.valueOf(i));
                contentStream.endText();
                contentStream.close();
            }
            testPdf1 = testStorage.resolve("test1.pdf");
            document.save(testPdf1.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (PDDocument document = new PDDocument()) {
            for (int i = 0; i < 5; i++) {
                PDPage page = new PDPage();
                document.addPage(page);
                PDPageContentStream contentStream = new PDPageContentStream(document, page);
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 192);
                contentStream.beginText();
                contentStream.showText(String.valueOf(i));
                contentStream.endText();
                contentStream.close();
            }
            testPdf2 = testStorage.resolve("test1.pdf");
            document.save(testPdf2.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        filenames = Arrays.asList(testPdf1.getFileName().toString(), testPdf2.getFileName().toString());
    }

    @Test
    @Order(1)
    void uploadFiles() throws Exception {
        MockMultipartFile mockMultipartFile1 = new MockMultipartFile(
                "file", testPdf1.getFileName().toString(),
                MediaType.APPLICATION_PDF_VALUE, Files.readAllBytes(testPdf1));
        MockMultipartFile mockMultipartFile2 = new MockMultipartFile(
                "file", testPdf2.getFileName().toString(),
                MediaType.APPLICATION_PDF_VALUE, Files.readAllBytes(testPdf2));

        List<String> covers = Arrays.asList("cover_file1.img", "cover_file2.img");
        List<MultipartFile> multipartFiles = Arrays.asList(mockMultipartFile1, mockMultipartFile2);
        MergeDto mergeDto = new MergeDto(covers, filenames);
        Mockito.when(mergeController.uploadFiles(multipartFiles, token)).thenReturn(mergeDto);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart("/merge-service/upload")
                .file((MockMultipartFile) multipartFiles.get(0))
                .file((MockMultipartFile) multipartFiles.get(1))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .characterEncoding("utf-8");
        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(covers)));
        Mockito.verify(mergeController).uploadFiles(multipartFiles, token);
    }

    @Test
    @Order(2)
    void merge() throws Exception {
        Resource resource = new UrlResource(testPdf1.toUri());
        ResponseEntity<Resource> responseEntity = ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filenames=\"" + resource.getFilename() + "\"").body(resource);

        Integer[] order = {1, 0};
        Mockito.when(mergeController.merge(filenames, token)).thenReturn(responseEntity);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/merge-service/merge")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filenames));
        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().bytes(Files.readAllBytes(Paths.get(resource.getFile().getPath()))));

        Mockito.verify(mergeController).merge(filenames, token);
    }
}
*/
