package com.example.pdf.service.pdf.interfaces;

import com.example.pdf.exception.PdfServiceException;

import java.nio.file.Path;
import java.util.List;

public interface MergeService {
    List<Path> renderFileCover(List<Path> uploadedFiles) throws PdfServiceException;

    Path merge(Integer[] order) throws PdfServiceException;
}
