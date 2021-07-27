package com.example.pdf.repository;

import com.example.pdf.domain.entity.Filename;
import com.example.pdf.domain.entity.Namespace;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@ActiveProfiles("test")
@DataJpaTest
public class FilenameRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    FilenameRepository filenameRepository;

    @Test
    void findByNameAndNamespaceName() {
        Namespace namespace = new Namespace("namespace_1");
        Filename filename = new Filename();
        filename.setId(1);
        filename.setName("filename_to_namespace_1");
        filename.setNamespace(namespace);
        entityManager.persist(namespace);
        entityManager.merge(filename);
        entityManager.flush();
        entityManager.clear();
        Optional<Filename> byNameAndNamespaceName = filenameRepository.findByNameAndNamespaceName(filename.getName(), namespace.getName());
        Assert.assertNotNull(byNameAndNamespaceName);
    }
}
