package com.example.pdf.repository;

import com.example.pdf.domain.entity.Filename;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface FilenameRepository extends JpaRepository<Filename, Integer> {
    @Query("select f from Filename f where f.name =:filename and f.namespace.name =:namespace")
    Optional<Filename> findByNameAndNamespaceName(String filename, String namespace);

    @Modifying
    @Query("delete from Filename f where f.id=:id")
    void deleteById(@Param("id") Integer id);
}
