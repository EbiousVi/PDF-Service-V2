package com.ebious.pdf.service.pdf.impl;

import com.ebious.pdf.service.pdf.interfaces.PdfMerger;
import com.ebious.pdf.service.storage.FileStorage;;
import com.ebious.pdf.domain.enums.DirName;
import com.ebious.pdf.domain.enums.Extension;
import com.ebious.pdf.domain.enums.Prefix;
import com.ebious.pdf.exception.PdfServiceException;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Service
public class PdfMergerImpl implements PdfMerger {

    private final static Logger logger = LoggerFactory.getLogger(PdfMergerImpl.class);
    private final FileStorage fileStorage;

    @Autowired
    public PdfMergerImpl(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    @Override
    public Path merge(String registryId, List<String> filenames) {
        Path uploadDir = fileStorage.getStorageSpace(registryId, DirName.UPLOAD);
        String filename = Prefix.MERGED.name + Extension.PDF.name;
        Path merge = fileStorage.getStorageSpace(registryId, DirName.MERGE).resolve(filename);
        PDFMergerUtility merger = new PDFMergerUtility();
        merger.setDestinationFileName(merge.toString());
        try {
            for (String name : filenames) {
                merger.addSource(uploadDir.resolve(name).toFile());
            }
            merger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
            return merge;
        } catch (IOException e) {
            logger.error("Can't merge files", e);
            throw new PdfServiceException("Can't merge files!");
        }
    }
}
