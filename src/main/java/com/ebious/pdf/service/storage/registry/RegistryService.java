package com.ebious.pdf.service.storage.registry;

public interface RegistryService {

    String addEntry(String rootUploadDirName);

    String getEntry(String registryId);

    Boolean deleteEntry(String registryId);
}
