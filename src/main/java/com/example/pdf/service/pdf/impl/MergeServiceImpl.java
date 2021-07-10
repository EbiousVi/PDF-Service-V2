package com.example.pdf.service.pdf.impl;

import com.example.pdf.domain.enums.DirsAtRoot;
import com.example.pdf.domain.enums.Extension;
import com.example.pdf.domain.enums.Filename;
import com.example.pdf.exception.PdfServicesException;
import com.example.pdf.service.pdf.interfaces.MergeService;
import com.example.pdf.service.storage.PathManager;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class MergeServiceImpl implements MergeService {
    private final PathManager pathManager;

    @Autowired
    public MergeServiceImpl(PathManager pathManager) {
        this.pathManager = pathManager;
    }

    @Override
    public List<Path> renderImgToFront(List<Path> uploadedFiles) throws PdfServicesException {
        List<Path> renderImgs = new ArrayList<>();
        Path renderImgDirPath = pathManager.getDirAtRootByName(DirsAtRoot.RENDER_IMG);
        PDFRenderer render;
        for (Path uploadedFile : uploadedFiles) {
            File uploadedPDF = new File(uploadedFile.toString());
            try (PDDocument document = PDDocument.load(uploadedPDF)) {
                render = new PDFRenderer(document);
                BufferedImage image = render.renderImageWithDPI(0, 36);
                String filename = Filename.RENDER_PAGE.name + UUID.randomUUID().toString().substring(0, 8) + Extension.JPEG.name;
                Path renderImg = renderImgDirPath.resolve(filename);
                renderImgs.add(renderImg);
                ImageIO.write(image, "JPEG", new File(renderImg.toString()));
            } catch (IOException e) {
                throw new PdfServicesException("Can't render image from file = " + uploadedFile.getFileName(), e);
            }
        }
        return renderImgs;
    }

    @Override
    public Path merge(Integer[] order) throws PdfServicesException {
        Path mergedDirPath = pathManager.getDirAtRootByName(DirsAtRoot.MERGED);
        String filename = Filename.MERGED.name + Extension.PDF.name;
        Path mergedFilePath = mergedDirPath.resolve(filename);
        PDFMergerUtility merger = new PDFMergerUtility();
        merger.setDestinationFileName(mergedFilePath.toString());
        Map<Integer, Path> uploadedFiles = pathManager.getUploadedFiles();
        try {
            for (Integer ordinal : order) {
                File file = new File(uploadedFiles.get(ordinal).toString());
                merger.addSource(file);
            }
            merger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
            return mergedFilePath;
        } catch (IOException e) {
            throw new PdfServicesException("Can't merge file", e);
        }
    }
}
