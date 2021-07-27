package com.example.pdf.repository;

import com.example.pdf.domain.entity.Namespace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
class NamespaceRepositoryTest {
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    NamespaceRepository namespaceRepository;

    Namespace namespace;

    @BeforeEach
    void createTestNamespaceEntity() {
        String name = "namespace_1";
        namespace = new Namespace(name);
    }

    @Test
    void findByName() {
        entityManager.persistAndFlush(namespace);
        entityManager.clear();
        Namespace namespaceGet = namespaceRepository
                .findByName(namespace.getName())
                .orElseThrow(() -> new RuntimeException(""));
        assertEquals(namespace.getName(), namespaceGet.getName());
    }

    @Test
    void existsByName() {
        entityManager.persistAndFlush(namespace);
        entityManager.clear();
        Boolean exist = namespaceRepository.existsByName(namespace.getName());
        assertTrue(exist);
    }
}