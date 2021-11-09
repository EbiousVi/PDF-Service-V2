package com.example.pdf.service.pdf.interfaces;

import com.example.pdf.exception.PdfServiceException;
import com.example.pdf.exception.StorageException;

import java.nio.file.Path;
import java.util.List;

public interface ZipService {
    Path packToZip(List<Path> zipEntries) throws StorageException, PdfServiceException;
}
