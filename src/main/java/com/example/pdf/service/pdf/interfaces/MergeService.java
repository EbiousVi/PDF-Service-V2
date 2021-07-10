package com.example.pdf.service.pdf.interfaces;

import com.example.pdf.exception.PdfServicesException;

import java.nio.file.Path;
import java.util.List;

public interface MergeService {
    List<Path> renderImgToFront(List<Path> uploadedFiles) throws PdfServicesException;

    Path merge(Integer[] order) throws PdfServicesException;
}
