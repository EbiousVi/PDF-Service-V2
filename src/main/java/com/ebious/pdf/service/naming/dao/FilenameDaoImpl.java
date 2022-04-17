package com.ebious.pdf.service.naming.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class FilenameDaoImpl implements FilenameDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilenameDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Boolean save(String namespace, String filename) {
        String query = "INSERT INTO filename (namespace, name) VALUES (?, ?)";
        return jdbcTemplate.update(query, namespace, filename) == 1;
    }

    @Override
    public Boolean saveAll(String namespace, List<String> filenames) {
        String query = "INSERT INTO filename (namespace, name) VALUES (?, ?)";
        int[] results = jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {

            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, namespace);
                ps.setString(2, filenames.get(i));
            }

            public int getBatchSize() {
                return filenames.size();
            }
        });

        for (int res : results) {
            if (res != 1) return false;
        }
        return true;
    }

    @Override
    public Boolean delete(String namespace, String filename) {
        String query = "DELETE FROM filename WHERE namespace = ? AND filename.name = ?";
        return jdbcTemplate.update(query, namespace, filename) == 1;
    }
}
