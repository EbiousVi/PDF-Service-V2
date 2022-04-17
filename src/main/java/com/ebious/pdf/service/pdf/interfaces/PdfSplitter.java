package com.ebious.pdf.service.pdf.interfaces;

import java.nio.file.Path;
import java.util.List;

public interface PdfSplitter {

    Path splitBySelectedPages(String registryId, List<Integer> selectedPages);

    List<Path> splitBySinglePage(String registryId);
}
