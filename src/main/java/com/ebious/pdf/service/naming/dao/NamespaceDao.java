package com.ebious.pdf.service.naming.dao;


import com.ebious.pdf.domain.entity.Namespace;

import java.util.List;
import java.util.Optional;

public interface NamespaceDao {

    List<Namespace> findAll();

    Optional<Namespace> findByName(String name);

    Boolean save(String name);

    Boolean delete(String name);
}
