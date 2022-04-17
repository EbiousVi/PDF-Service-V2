package com.ebious.pdf.service.pdf.interfaces;

import java.nio.file.Path;
import java.util.List;

public interface PdfRender {

    List<Path> renderPageCovers(String registryId);

    List<Path> renderFileCovers(String registryId);
}
