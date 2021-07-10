package com.example.pdf.service.pdf.impl;

import com.example.pdf.domain.enums.DirsAtRoot;
import com.example.pdf.domain.enums.Extension;
import com.example.pdf.domain.enums.Filename;
import com.example.pdf.exception.PdfServicesException;
import com.example.pdf.exception.StorageException;
import com.example.pdf.service.pdf.interfaces.SplitService;
import com.example.pdf.service.pdf.interfaces.ZipService;
import com.example.pdf.service.storage.PathManager;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class SplitServiceImpl implements SplitService, ZipService {
    private final PathManager pathManager;
    private Path zipFilePath;

    @Autowired
    public SplitServiceImpl(PathManager pathManager) {
        this.pathManager = pathManager;
    }

    @Override
    public Path splitBySelectedPages(Integer[] selectedPages) throws StorageException {
        File uploadedPDF = new File(pathManager.getUploadedFile().toString());
        try (PDDocument splittedDocument = new PDDocument();
             PDDocument uploadDocument = PDDocument.load(uploadedPDF)) {
            StringBuilder pageNumbers = new StringBuilder();
            for (Integer pageNumber : selectedPages) {
                splittedDocument.addPage(uploadDocument.getPage(pageNumber));
                pageNumbers.append(pageNumber).append(",");
            }
            pageNumbers.deleteCharAt(pageNumbers.lastIndexOf(","));
            Path splittedDir = pathManager.getDirAtRootByName(DirsAtRoot.SPLITTED);
            String filename = Filename.SPLITTED_PAGES.name + pageNumbers + Extension.PDF.name;
            Path splittedFile = splittedDir.resolve(filename);
            splittedDocument.save(splittedFile.toString());
            return splittedFile;
        } catch (IOException e) {
            throw new StorageException("Can't split " + pathManager.getUploadedFile(), e);
        }
    }

    @Override
    public List<Path> splitAll() throws PdfServicesException {
        File uploadedPDF = new File(pathManager.getUploadedFile().toString());
        try (PDDocument source = PDDocument.load(uploadedPDF)) {
            Splitter splitter = new Splitter();
            List<PDDocument> splitAll = splitter.split(source);
            List<Path> singlePages = new ArrayList<>();
            Path splitAllDir = pathManager.getDirAtRootByName(DirsAtRoot.SPLIT_ALL);
            for (int i = 0; i < splitAll.size(); i++) {
                try (PDDocument pdDocument = splitAll.get(i)) {
                    String filename = Filename.PAGE.name + i + Extension.PDF.name;
                    Path singlePage = splitAllDir.resolve(filename);
                    pdDocument.save(singlePage.toString());
                    singlePages.add(singlePage);
                }
            }
            return singlePages;
        } catch (IOException e) {
            throw new PdfServicesException("Can't split all " + pathManager.getUploadedFile(), e);
        }
    }

    public Path getZipOfPages() throws PdfServicesException {
        if (zipFilePath != null) return zipFilePath;
        return this.packToZip(this.splitAll());
    }

    @Override
    public Path packToZip(List<Path> zipEntries) throws PdfServicesException {
        Path zipDir = pathManager.getDirAtRootByName(DirsAtRoot.ZIP);
        String filename = Filename.ZIP.name + Extension.ZIP.name;
        Path zipPath = zipDir.resolve(filename);
        try {
            try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(zipPath.toString()))) {
                for (Path entry : zipEntries) {
                    try (FileInputStream fis = new FileInputStream(entry.toString())) {
                        ZipEntry zipEntry = new ZipEntry(entry.getFileName().toString());
                        zout.putNextEntry(zipEntry);
                        byte[] buffer = new byte[2048];
                        int length;
                        while ((length = fis.read(buffer)) >= 0) {
                            zout.write(buffer, 0, length);
                        }
                        zout.write(buffer);
                        zout.closeEntry();
                    }
                }
                zipFilePath = zipPath;
                return zipPath;
            }
        } catch (IOException e) {
            throw new PdfServicesException("Can't create zip ", e);
        }
    }

    @Override
    public List<Path> renderImgToFront(Path uploadedFile) throws PdfServicesException {
        List<Path> renderImgs = new ArrayList<>();
        File uploadedPDF = new File(uploadedFile.toString());
        try (PDDocument document = PDDocument.load(uploadedPDF)) {
            PDFRenderer render = new PDFRenderer(document);
            Path imgDir = pathManager.getDirAtRootByName(DirsAtRoot.RENDER_IMG);
            for (int i = 0; i < document.getNumberOfPages(); i++) {
                BufferedImage image = render.renderImageWithDPI(i, 36);
                String filename = Filename.RENDER_PAGE.name + i + "_" + UUID.randomUUID().toString().substring(0, 8) + Extension.JPEG.name;
                Path renderImg = imgDir.resolve(filename);
                renderImgs.add(renderImg);
                ImageIO.write(image, "JPEG", new File(renderImg.toString()));
            }
            return renderImgs;
        } catch (Exception e) {
            throw new PdfServicesException("Can't render image from file = " + uploadedFile.getFileName(), e);
        }
    }
}
