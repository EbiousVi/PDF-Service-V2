package com.example.pdf.service.pdf.interfaces;

import com.example.pdf.exception.PdfServicesException;
import com.example.pdf.exception.StorageException;

import java.nio.file.Path;
import java.util.List;

public interface SplitService {
    List<Path> renderImgToFront(Path uploadedFile) throws PdfServicesException;

    Path splitBySelectedPages(Integer[] pages) throws StorageException;

    List<Path> splitAll() throws PdfServicesException;
}
