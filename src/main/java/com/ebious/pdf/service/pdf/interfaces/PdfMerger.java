package com.ebious.pdf.service.pdf.interfaces;

import java.nio.file.Path;
import java.util.List;

public interface PdfMerger {

    Path merge(String registryId, List<String> filenames);
}
