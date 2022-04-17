package com.ebious.pdf.service.naming.exporter;

import java.nio.file.Path;
import java.util.List;

public interface FileExporter<T> {
    Path export(List<T> data);
}
