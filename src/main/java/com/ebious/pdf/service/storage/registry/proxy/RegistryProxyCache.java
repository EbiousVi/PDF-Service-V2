package com.ebious.pdf.service.storage.registry.proxy;

import com.ebious.pdf.service.storage.registry.RegistryServiceImpl;
import com.ebious.pdf.service.storage.registry.RegistryService;
import com.google.common.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistryProxyCache implements RegistryService {

    private final RegistryServiceImpl registryService;
    private final Cache<String, String> cache;

    @Autowired
    public RegistryProxyCache(RegistryServiceImpl registryService, Cache<String, String> cache) {
        this.registryService = registryService;
        this.cache = cache;
    }

    @Override
    public String addEntry(String rootUploadDirName) {
        String registryId = registryService.addEntry(rootUploadDirName);
        cache.put(registryId, rootUploadDirName);
        return registryId;
    }

    @Override
    public String getEntry(String registryId) {
        String path = cache.getIfPresent(registryId);
        if (path == null) {
            return registryService.getEntry(registryId);
        }
        return path;
    }

    @Override
    public Boolean deleteEntry(String registryId) {
        cache.invalidate(registryId);
        return registryService.deleteEntry(registryId);
    }
}
