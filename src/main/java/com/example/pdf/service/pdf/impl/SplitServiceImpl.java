package com.example.pdf.service.pdf.impl;

import com.example.pdf.domain.enums.DirsAtRoot;
import com.example.pdf.domain.enums.Extension;
import com.example.pdf.domain.enums.Filename;
import com.example.pdf.exception.PdfServiceException;
import com.example.pdf.service.pdf.interfaces.SplitService;
import com.example.pdf.service.pdf.interfaces.ZipService;
import com.example.pdf.service.storage.PathManager;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class SplitServiceImpl implements SplitService, ZipService {

    private final PathManager pathManager;

    @Autowired
    public SplitServiceImpl(PathManager pathManager) {
        this.pathManager = pathManager;
    }

    @Override
    public Path splitBySelectedPages(Integer[] selectedPages) throws PdfServiceException {
        try (PDDocument splittedDocument = new PDDocument();
             PDDocument uploadedDocument = PDDocument.load(pathManager.getUploadedFile().toFile())) {
            StringBuilder pageNumbers = new StringBuilder();
            for (Integer pageNumber : selectedPages) {
                splittedDocument.addPage(uploadedDocument.getPage(pageNumber));
                pageNumbers.append(pageNumber).append("-");
            }
            pageNumbers.deleteCharAt(pageNumbers.lastIndexOf("-"));
            String filename = Filename.SPLITTED_PAGES.name + pageNumbers + Extension.PDF.name;
            Path splittedDir = pathManager.getDirAtRootByName(DirsAtRoot.SPLIT);
            Path splittedFile = splittedDir.resolve(filename);
            splittedDocument.save(splittedFile.toString());
            return splittedFile;
        } catch (IOException e) {
            e.printStackTrace();
            throw new PdfServiceException("Can't split " + pathManager.getUploadedFile().getFileName());
        }
    }

    @Override
    public List<Path> splitBySinglePages() throws PdfServiceException {
        try (PDDocument document = PDDocument.load(pathManager.getUploadedFile().toFile())) {
            Splitter splitter = new Splitter();
            List<PDDocument> splitAll = splitter.split(document);
            List<Path> singlePages = new ArrayList<>();
            Path splitAllDir = pathManager.getDirAtRootByName(DirsAtRoot.SPLIT_ALL);
            for (int i = 0; i < splitAll.size(); i++) {
                try (PDDocument pdDocument = splitAll.get(i)) {
                    String filename = Filename.SINGLE_PAGE.name + i + Extension.PDF.name;
                    Path singlePage = splitAllDir.resolve(filename);
                    pdDocument.save(singlePage.toString());
                    singlePages.add(singlePage);
                }
            }
            return singlePages;
        } catch (IOException e) {
            e.printStackTrace();
            throw new PdfServiceException("Can't split all " + pathManager.getUploadedFile().getFileName());
        }
    }

    @Override
    public Path packToZip(List<Path> zipEntries) throws PdfServiceException {
        Path zipDir = pathManager.getDirAtRootByName(DirsAtRoot.ZIP);
        String filename = Filename.ZIP.name + Extension.ZIP.name;
        Path zip = zipDir.resolve(filename);
        try {
            try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(zip.toString()))) {
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
                return zip;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new PdfServiceException("Can't create zip");
        }
    }

    /**
     * Method work slowly with scanned files. ~ 0.5-1 second need to complete ImageIo.write
     * I have not found a way to speed up this process.
     */
    @Override
    public List<Path> renderFilePages(Path uploadedFile) throws PdfServiceException {
        List<Path> renderingPages = new ArrayList<>();
        try (PDDocument document = PDDocument.load(uploadedFile.toFile())) {
            PDFRenderer render = new PDFRenderer(document);
            Path renderingPagesDir = pathManager.getDirAtRootByName(DirsAtRoot.RENDER_IMG);
            for (int i = 0; i < document.getNumberOfPages(); i++) {
                BufferedImage image = render.renderImageWithDPI(i, 36);
                String filename = Filename.IMG_PAGE.name + i + "_" +
                        uploadedFile.getFileName().toString().replaceFirst("\\.[^.]+$", "") + "_" +
                        UUID.randomUUID().toString().substring(0, 8) + Extension.JPEG.name;
                Path renderingPage = renderingPagesDir.resolve(filename);
                ImageIOUtil.writeImage(image, renderingPage.toAbsolutePath().toString(), 36);
                renderingPages.add(renderingPage);
            }
            return renderingPages;
        } catch (Exception e) {
            e.printStackTrace();
            throw new PdfServiceException("Can't render image from file " + uploadedFile.getFileName().getFileName());
        }
    }
}
