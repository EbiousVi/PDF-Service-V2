package com.example.pdf.repository;

import com.example.pdf.domain.entity.Filename;
import com.example.pdf.domain.entity.Namespace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FilenameRepository extends JpaRepository<Filename, Integer> {
    List<Filename> findAllByNamespace(Namespace namespace);

    Filename findByFilename(String name);
}
