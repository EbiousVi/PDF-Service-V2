package com.ebious.pdf.service.naming.jdbc;

import com.ebious.pdf.domain.entity.Namespace;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NamespaceResultSetExtractor implements ResultSetExtractor<List<Namespace>> {

    private static final String NAMESPACE_ID = "namespace.id";
    private static final String NAMESPACE_NAME = "namespace.name";
    private static final String FILENAME_NAME = "filename.name";
    private final List<Namespace> namespaces;
    private Namespace curNamespace;

    public NamespaceResultSetExtractor() {
        this.namespaces = new ArrayList<>();
    }

    @Override
    public List<Namespace> extractData(ResultSet rs) throws SQLException, DataAccessException {
        while (rs.next()) {
            long id = rs.getLong(NAMESPACE_ID);
            if (curNamespace == null) { // initial object
                curNamespace = mapToNamespace(rs);
            } else if (curNamespace.getId() != id) { // break
                namespaces.add(curNamespace);
                curNamespace = mapToNamespace(rs);
            }
            curNamespace.addFilename(mapToFilename(rs));
        }

        if (curNamespace != null) { // last object
            namespaces.add(curNamespace);
        }
        return namespaces;
    }

    private String mapToFilename(ResultSet rs) throws SQLException {
        return rs.getString(FILENAME_NAME);
    }

    private Namespace mapToNamespace(ResultSet rs) throws SQLException {
        return new Namespace(Long.valueOf(rs.getString(NAMESPACE_ID)), rs.getString(NAMESPACE_NAME));
    }
}
