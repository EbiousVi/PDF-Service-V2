/*
package com.example.pdf.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SplitController.class)
class SplitControllerWebMvcTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    SplitController splitController;

    @MockBean
    CommandLineRunner commandLineRunner;

    @TempDir
    static Path testStorage;

    static Path testPdf;

    @BeforeAll
    static void createTestPdf() {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            testPdf = testStorage.resolve("test.pdf");
            document.save(testPdf.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void splitBySelectedPages() throws Exception {
        Resource resource = new UrlResource(testPdf.toUri());
        ResponseEntity<Resource> responseEntity = ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filenames=\"" + resource.getFilename() + "\"").body(resource);

        Integer[] selectedPages = {1};
        Mockito.when(splitController.splitBySelectedPages(selectedPages)).thenReturn(responseEntity);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/split-service/split-by-selected-pages")
                .characterEncoding("utf-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Arrays.toString(selectedPages));
        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
        Mockito.verify(splitController).splitBySelectedPages(selectedPages);
    }

    @Test
    void uploadFile() throws Exception {
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "file", testPdf.getFileName().toString(),
                MediaType.APPLICATION_PDF_VALUE, Files.readAllBytes(testPdf));
        List<String> renderingImgs = Arrays.asList("page_1.img", "page_2.img");

        Mockito.when(splitController.uploadFile(mockMultipartFile)).thenReturn(renderingImgs);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .multipart("/split-service/upload")
                .file(mockMultipartFile)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .characterEncoding("utf-8");
        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(renderingImgs)));
        Mockito.verify(splitController).uploadFile(mockMultipartFile);
    }

    @Test
    void splitBySinglePages() throws Exception {
        Resource resource = new UrlResource(testPdf.toUri());
        ResponseEntity<Resource> responseEntity = ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filenames=\"" + resource.getFilename() + "\"").body(resource);

        Mockito.when(splitController.splitBySinglePages()).thenReturn(responseEntity);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/split-service/split-by-single-pages")
                .characterEncoding("utf-8");
        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().bytes(Files.readAllBytes(Paths.get(resource.getFile().getPath()))));
        Mockito.verify(splitController).splitBySinglePages();
    }
}*/
