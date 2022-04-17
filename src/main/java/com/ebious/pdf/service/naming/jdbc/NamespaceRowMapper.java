package com.ebious.pdf.service.naming.jdbc;

import com.ebious.pdf.domain.entity.Namespace;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class NamespaceRowMapper implements RowMapper<Namespace> {

    private static final String NAMESPACE_ID = "namespace.id";
    private static final String NAMESPACE_NAME = "namespace.name";

    @Override
    public Namespace mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Namespace(rs.getLong(NAMESPACE_ID), rs.getString(NAMESPACE_NAME));
    }
}
