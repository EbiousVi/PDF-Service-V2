package com.ebious.pdf.service.naming.dao;

import com.ebious.pdf.service.naming.jdbc.NamespaceResultSetExtractor;
import com.ebious.pdf.service.naming.jdbc.NamespaceRowMapper;
import com.ebious.pdf.domain.entity.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class NamespaceDaoImpl implements NamespaceDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public NamespaceDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Namespace> findAll() {
        String query = "SELECT * FROM namespace INNER JOIN filename ON namespace.name = filename.namespace";
        return jdbcTemplate.query(query, new NamespaceResultSetExtractor());
    }

    @Override
    public Optional<Namespace> findByName(String name) {
        String query = "SELECT * FROM namespace WHERE name = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(query, new NamespaceRowMapper(), name));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Boolean save(String name) {
        try {
            String query = "INSERT INTO namespace (name) VALUES (?)";
            return jdbcTemplate.update(query, name) == 1;
        } catch (DuplicateKeyException e) {
            return false;
        }
    }

    @Override
    public Boolean delete(String name) {
        String query = "DELETE FROM namespace WHERE namespace.name = ?";
        return jdbcTemplate.update(query, name) == 1;
    }
}