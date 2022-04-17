package com.ebious.pdf.service.pdf.impl;

import com.ebious.pdf.service.pdf.interfaces.PdfSplitter;
import com.ebious.pdf.service.storage.FileStorage;
import com.ebious.pdf.service.storage.FileStorageImpl;
import com.ebious.pdf.domain.enums.DirName;
import com.ebious.pdf.domain.enums.Extension;
import com.ebious.pdf.domain.enums.Prefix;
import com.ebious.pdf.exception.PdfServiceException;
import com.ebious.pdf.exception.StorageException;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
public class PdfSplitterImpl implements PdfSplitter {

    private final static Logger logger = LoggerFactory.getLogger(PdfSplitterImpl.class);
    private final FileStorage fileStorage;

    @Autowired
    public PdfSplitterImpl(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    @Override
    public Path splitBySelectedPages(String registryId, List<Integer> selectedPages) {
        Path uploadedFile = fileStorage.getStorageFile(registryId, DirName.UPLOAD);
        try (PDDocument source = PDDocument.load(uploadedFile.toFile());
             PDDocument destination = new PDDocument()) {
            StringBuilder pageNumbers = new StringBuilder();
            for (Integer pageNo : selectedPages) {
                destination.addPage(source.getPage(pageNo));
                pageNumbers.append(pageNo).append("-");
            }
            pageNumbers.deleteCharAt(pageNumbers.lastIndexOf("-"));
            String filename = getFilename(Prefix.PAGES, pageNumbers.toString());
            Path split = fileStorage.getStorageSpace(registryId, DirName.SPLIT).resolve(filename);
            destination.save(split.toString());
            return split;
        } catch (IOException | StorageException e) {
            logger.error("Can not split", e);
            throw new PdfServiceException("Can't split file");
        }
    }

    @Override
    public List<Path> splitBySinglePage(String registryId) {
        Path uploadedFile = fileStorage.getStorageFile(registryId, DirName.UPLOAD);
        try (PDDocument source = PDDocument.load(uploadedFile.toFile())) {
            Splitter splitter = new Splitter();
            List<PDDocument> splitAll = splitter.split(source);
            List<Path> pathList = new ArrayList<>();
            Path splitAllDir = fileStorage.getStorageSpace(registryId, DirName.SPLIT_ALL);
            for (int i = 0; i < splitAll.size(); i++) {
                try (PDDocument singlePageDoc = splitAll.get(i)) {
                    String filename = getFilename(Prefix.PAGE, String.valueOf(i));
                    Path split = splitAllDir.resolve(filename);
                    singlePageDoc.save(split.toString());
                    pathList.add(split);
                } catch (IOException e) {
                    logger.error("Can not split doc by single pages", e);
                    throw new PdfServiceException("Can not split = " + uploadedFile.getFileName() + " by single pages!");
                }
            }
            return pathList;
        } catch (IOException e) {
            logger.error("Can not split doc by single pages", e);
            throw new PdfServiceException("Can not split = " + uploadedFile.getFileName() + " by single pages!");
        }
    }

    private String getFilename(Prefix prefix, String body) {
        return prefix.name + body + Extension.PDF.name;
    }
}