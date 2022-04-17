package com.ebious.pdf.service.zip;

import com.ebious.pdf.domain.enums.DirName;
import com.ebious.pdf.domain.enums.Extension;
import com.ebious.pdf.domain.enums.Prefix;
import com.ebious.pdf.exception.ZipServiceException;
import com.ebious.pdf.service.storage.FileStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ZipArchiverImpl implements Archiver {

    private final static Logger logger = LoggerFactory.getLogger(ZipArchiverImpl.class);
    private final FileStorage fileStorage;

    @Autowired
    public ZipArchiverImpl(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    public Path archive(String registryId, DirName dirName) {
        String filename = Prefix.ZIP.name + Extension.ZIP.name;
        Path zipFile = fileStorage.getStorageSpace(registryId, DirName.ZIP).resolve(filename);
        List<Path> files = fileStorage.getStorageFiles(registryId, dirName);
        try {
            try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(zipFile.toString()))) {
                for (Path entry : files) {
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
                return zipFile;
            }
        } catch (IOException e) {
            logger.error("Can not write zip archive!", e);
            throw new ZipServiceException("Can not pack files to zip!");
        }
    }
}
