package com.example.pdf.repository;

import com.example.pdf.domain.entity.Namespace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NamespaceRepository extends JpaRepository<Namespace, Integer> {
    Namespace findByNamespace(String name);
}
