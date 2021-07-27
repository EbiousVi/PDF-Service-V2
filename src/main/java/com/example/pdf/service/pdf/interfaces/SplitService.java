package com.example.pdf.service.pdf.interfaces;

import com.example.pdf.exception.PdfServiceException;

import java.nio.file.Path;
import java.util.List;

public interface SplitService {
    List<Path> renderFilePages(Path uploadedFile) throws PdfServiceException;

    Path splitBySelectedPages(Integer[] pages) throws PdfServiceException;

    List<Path> splitBySinglePages() throws PdfServiceException;
}
