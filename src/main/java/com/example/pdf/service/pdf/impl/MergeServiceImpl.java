package com.example.pdf.service.pdf.impl;

import com.example.pdf.domain.enums.DirsAtRoot;
import com.example.pdf.domain.enums.Extension;
import com.example.pdf.domain.enums.Filename;
import com.example.pdf.exception.PdfServiceException;
import com.example.pdf.service.pdf.interfaces.MergeService;
import com.example.pdf.service.storage.PathManager;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MergeServiceImpl implements MergeService {
    private final PathManager pathManager;

    @Autowired
    public MergeServiceImpl(PathManager pathManager) {
        this.pathManager = pathManager;
    }

    @Override
    public List<Path> renderFileCover(List<Path> uploadedFiles) throws PdfServiceException {
        List<Path> renderingCovers = new ArrayList<>();
        Path renderingCoversDir = pathManager.getDirAtRootByName(DirsAtRoot.RENDER_IMG);
        PDFRenderer render;
        for (Path uploadedFile : uploadedFiles) {
            try (PDDocument document = PDDocument.load(uploadedFile.toFile())) {
                render = new PDFRenderer(document);
                BufferedImage image = render.renderImageWithDPI(0, 36);
                String filename = Filename.IMG_COVER.name +
                        uploadedFile.getFileName().toString().replaceFirst("\\.[^.]+$", "") + "_" +
                        UUID.randomUUID().toString().substring(0, 8) + Extension.JPEG.name;
                Path renderingCover = renderingCoversDir.resolve(filename);
                ImageIOUtil.writeImage(image, renderingCover.toAbsolutePath().toString(), 36);
                renderingCovers.add(renderingCover);
            } catch (IOException e) {
                e.printStackTrace();
                throw new PdfServiceException("Can't render image from file = " + uploadedFile.getFileName());
            }
        }
        return renderingCovers;
    }

    @Override
    public Path merge(Integer[] order) throws PdfServiceException {
        Path mergedDir = pathManager.getDirAtRootByName(DirsAtRoot.MERGE);
        String filename = Filename.MERGED.name + Extension.PDF.name;
        Path mergedFile = mergedDir.resolve(filename);
        PDFMergerUtility merger = new PDFMergerUtility();
        merger.setDestinationFileName(mergedFile.toString());
        try {
            for (Integer ordinal : order) {
                merger.addSource(pathManager.getUploadedFileByOrdinal(ordinal).toFile());
            }
            merger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
            return mergedFile;
        } catch (IOException e) {
            e.printStackTrace();
            throw new PdfServiceException("Can't merge file!");
        }
    }
}
