package com.ebious.pdf.service.pdf.impl;

import com.ebious.pdf.domain.enums.DirName;
import com.ebious.pdf.domain.enums.Extension;
import com.ebious.pdf.domain.enums.Prefix;
import com.ebious.pdf.exception.PdfServiceException;
import com.ebious.pdf.service.pdf.interfaces.PdfRender;
import com.ebious.pdf.service.storage.FileStorage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

@Service
public class RenderPdfToJpegImpl implements PdfRender {

    private static final Logger logger = LoggerFactory.getLogger(RenderPdfToJpegImpl.class);
    private static final int DPI = 72;
    private static final int PAGE_COVER_INDEX = 0;
    private final FileStorage fileStorage;

    @Autowired
    public RenderPdfToJpegImpl(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    /**
     * Method work slowly with scanned files. ~ 0.5-1 second need to complete {@link ImageIOUtil}
     * I have not found a way to speed up this process.
     */
    @Override
    public List<Path> renderPageCovers(String registryId) {
        Path uploadedFile = fileStorage.getStorageFile(registryId, DirName.UPLOAD);
        List<Path> pageCovers = new ArrayList<>();
        Path renderDir = fileStorage.getStorageSpace(registryId, DirName.RENDER);
        try (PDDocument source = PDDocument.load(uploadedFile.toFile())) {
            PDFRenderer render = new PDFRenderer(source);
            for (int pageNo = 0; pageNo < source.getNumberOfPages(); pageNo++) {
                BufferedImage image = render.renderImageWithDPI(pageNo, DPI);
                String filename = generateImageFilename(Prefix.PAGE_COVER);
                Path pageCover = renderDir.resolve(filename);
                ImageIOUtil.writeImage(image, pageCover.toAbsolutePath().toString(), DPI);
                pageCovers.add(pageCover);
            }
            return pageCovers;
        } catch (Exception e) {
            logger.error("Can't render page covers from file = {}", uploadedFile.getFileName(), e);
            throw new PdfServiceException("Can't render page covers from file = " + uploadedFile.getFileName());
        }
    }

    @Override
    public List<Path> renderFileCovers(String registryId) {
        List<Path> uploadedFiles = fileStorage.getStorageFiles(registryId, DirName.UPLOAD);
        List<Path> fileCovers = new ArrayList<>();
        Path renderDir = fileStorage.getStorageSpace(registryId, DirName.RENDER);
        PDFRenderer render;
        for (Path uploadedFile : uploadedFiles) {
            try (PDDocument source = PDDocument.load(uploadedFile.toFile())) {
                render = new PDFRenderer(source);
                BufferedImage image = render.renderImageWithDPI(PAGE_COVER_INDEX, DPI);
                String filename = generateImageFilename(Prefix.FILE_COVER);
                Path fileCover = renderDir.resolve(filename);
                ImageIOUtil.writeImage(image, fileCover.toAbsolutePath().toString(), DPI);
                fileCovers.add(fileCover);
            } catch (IOException e) {
                logger.error("Can't render file cover from file = {}", uploadedFile.getFileName(), e);
                throw new PdfServiceException("Can't render file cover from file = " + uploadedFile.getFileName());
            }
        }
        return fileCovers;
    }

    private String generateImageFilename(Prefix prefix) {
        return prefix.name + generateUniqueImageName() + Extension.JPEG.name;
    }

    /**
     * Image should have a unique filenames. To uploading file as resouce from server.
     */
    private String generateUniqueImageName() {
        return UUID.randomUUID() + "_" + System.currentTimeMillis();
    }
}
