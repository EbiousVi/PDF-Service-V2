package com.ebious.pdf.service.storage.registry;

import com.ebious.pdf.exception.RegistryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Registry service for storage. Store registry id and root upload directory name;
 */
@Service
public class RegistryServiceImpl implements RegistryService {

    private final static Logger logger = LoggerFactory.getLogger(RegistryServiceImpl.class);
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RegistryServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String addEntry(String rootUploadDirName) {
        String registryId = generateRegistryId();
        String query = "INSERT INTO registry (registryId, userDirName) VALUES (?, ?)";
        try {
            int update = jdbcTemplate.update(query, registryId, rootUploadDirName);
            if (update == 1) return registryId;
        } catch (DuplicateKeyException e) {
            logger.error("Can not add new registry entry. Duplicate registry id, id = {}," +
                    " root upload dir = {}", registryId, rootUploadDirName, e);
        }
        throw new RegistryException("Storage not available now, try again!");
    }

    @Override
    public String getEntry(String registryId) {
        String sql = "SELECT userDirName FROM registry WHERE registryId = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, registryId);
        } catch (EmptyResultDataAccessException e) {
            logger.warn("Attempt to access by non-existent registry id", e);
            throw new RegistryException("Attempt to access by non-existent registry id");
        }
    }

    @Override
    public Boolean deleteEntry(String registryId) {
        String query = "DELETE FROM registry WHERE registryId = ?";
        return jdbcTemplate.update(query, registryId) == 1;
    }

    private String generateRegistryId() {
        return UUID.randomUUID() + "-" + System.currentTimeMillis();
    }
}
