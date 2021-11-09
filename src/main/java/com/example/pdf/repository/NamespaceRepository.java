package com.example.pdf.repository;

import com.example.pdf.domain.entity.Namespace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NamespaceRepository extends JpaRepository<Namespace, Integer> {
    Optional<Namespace> findByName(String name);

    Boolean existsByName(String name);
}
