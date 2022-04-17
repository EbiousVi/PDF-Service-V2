package com.ebious.pdf.service.zip;

import com.ebious.pdf.domain.enums.DirName;

import java.nio.file.Path;

public interface Archiver {

    Path archive(String registryId, DirName dirName);
}
