package com.example.pdf.controller;

import com.example.pdf.domain.dto.AddFormDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NamingControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    NamingController namingController;

    String requestParam = "namespace";
    Integer id = 1;
    String namespace = "namespace_1";
    String filename = "filename_to_namespace_1";

    @Test
    @Order(0)
    void addNamespace() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/naming/add-namespace")
                .param(requestParam, namespace);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    @Order(1)
    void getNamespaceByName() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/naming/namespace")
                .param(requestParam, namespace)
                .characterEncoding("utf-8");
        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(namespace));
    }

    @Test
    @Order(2)
    void getAllNamespaces() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/naming/namespaces")
                .characterEncoding("utf-8");
        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(Collections.singletonList(namespace))));
    }

    @Test
    @Order(3)
    void addFilenameToNamespace() throws Exception {
        AddFormDto addFormDto = new AddFormDto(namespace, filename);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/naming/add-filename")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addFormDto))
                .characterEncoding("utf-8");
        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    @Order(4)
    void getFilenamesByNamespace() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/naming/filenames-by-namespace")
                .param(requestParam, namespace);
        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(Collections.singletonList(filename))));

    }

    @Test
    @Order(5)
    void getAllNamespacesAndTheirFilenames() throws Exception {
        Map<String, List<String>> map = new HashMap<>();
        map.put(namespace, Collections.singletonList(filename));
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/naming/namespace-and-filenames");
        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(map)));
    }

    @Test
    @Order(6)
    void deleteFilename() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/naming/delete-filename/{namespace}/{filename}", namespace, filename);
        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    @Order(7)
    void deleteNamespace() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/naming/delete-namespace/{namespace}", namespace);
        mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }
}