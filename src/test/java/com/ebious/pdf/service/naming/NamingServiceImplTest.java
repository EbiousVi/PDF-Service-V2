package com.ebious.pdf.service.naming;

import com.ebious.pdf.TestData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.ebious.pdf.TestData.expectedNamespaceWithoutFilenames;
import static com.ebious.pdf.TestData.newFilename;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class NamingServiceImplTest {

    private static File testJson;
    private static MultipartFile mockFile;

    @Autowired
    NamingService namingService;

    @BeforeAll
    static void init() throws IOException {
        testJson = new File("src/test/resources/test.json");
        mockFile = new MockMultipartFile(
                "file", testJson.getName(),
                MediaType.APPLICATION_JSON_VALUE, Files.readAllBytes(testJson.toPath()));
    }

    @Test
    void collectAllNamespaces() {
        Map<String, List<String>> map = namingService.collectNamespacesAndFilenames();
        assertThat(map)
                .contains(entry(TestData.expectedNamespace1.getName(),
                        Arrays.asList(TestData.expected_filename1_Namespace1.getName(), TestData.expected_filename2_Namespace1.getName())))
                .contains(entry(TestData.expectedNamespace2.getName(),
                        Arrays.asList(TestData.expected_filename1_Namespace2.getName(), TestData.expected_filename2_Namespace2.getName())))
                .contains(entry(TestData.expectedNamespace3.getName(),
                        Arrays.asList(TestData.expected_filename1_Namespace3.getName(), TestData.expected_filename2_Namespace3.getName())));
    }

    @Test
    void addFilenameToNamespace() {
        Boolean add = namingService.addFilenameToNamespace(expectedNamespaceWithoutFilenames.getName(), newFilename.getName());
        assertThat(add).isTrue();
    }

    @Test
    void addNamespace() {
        Boolean add = namingService.addNamespace(TestData.newNamespace.getName());
        assertThat(add).isTrue();
    }

    @Test
    void getAllNamespaces() {
        List<String> allNamespaces = namingService.getAllNamespaceNames();
        assertThat(allNamespaces)
                .hasSize(3)
                .contains(TestData.expectedNamespace1.getName(),
                        TestData.expectedNamespace2.getName(),
                        TestData.expectedNamespace3.getName());
    }

    @Test
    void deleteNamespace() {
        Boolean delete = namingService.deleteNamespace(expectedNamespaceWithoutFilenames.getName());
        assertThat(delete).isTrue();
    }

    @Test
    void deleteFilename() {
        Boolean delete = namingService.deleteFilename(TestData.expectedNamespace2.getName(), TestData.expected_filename1_Namespace2.getName());
        assertThat(delete).isTrue();
    }

    @Test
    void importFromFile() {
        namingService.importFromFile(mockFile);
    }

    @Test
    void export() {
        Path export = namingService.exportToFile();
        assertThat(export).exists();
    }

    @Test
    void loadFromJson() {
        namingService.importFromFile(mockFile);
    }
}