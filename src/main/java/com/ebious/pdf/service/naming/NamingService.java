package com.ebious.pdf.service.naming;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public interface NamingService {

    Boolean addNamespace(String namespace);

    Boolean addFilenameToNamespace(String namespace, String filename);

    Boolean deleteNamespace(String namespace);

    Boolean deleteFilename(String filename, String namespace);

    Map<String, List<String>> collectNamespacesAndFilenames();

    List<String> getAllNamespaceNames();

    Path exportToFile();

    void importFromFile(MultipartFile multipartFile);
}
