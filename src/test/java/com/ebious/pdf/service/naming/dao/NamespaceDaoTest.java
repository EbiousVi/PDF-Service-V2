package com.ebious.pdf.service.naming.dao;

import com.ebious.pdf.TestData;
import com.ebious.pdf.domain.entity.Namespace;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class NamespaceDaoTest {

    @Autowired
    NamespaceDao namespaceDao;

    @Test
    void findAll() {
        List<Namespace> namespaces = namespaceDao.findAll();
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(namespaces).hasSize(3);
        namespaces.forEach(namespace -> softAssertions.assertThat(namespace.getFilenames()).hasSize(2));
        softAssertions.assertAll();
    }

    @Test
    void findByName() {
        Optional<Namespace> namespace = namespaceDao.findByName(TestData.expectedNamespace1.getName());
        assertThat(namespace)
                .isPresent()
                .get()
                .hasFieldOrPropertyWithValue("name", TestData.expectedNamespace1.getName());
    }

    @Test
    void findByNameNotFoundReturnEmpty() {
        Optional<Namespace> empty = namespaceDao.findByName(TestData.notExistNamespace.getName());
        assertThat(empty).isEmpty();
    }

    @Test
    void saveExistsEntry() {
        Boolean save = namespaceDao.save(TestData.expectedNamespace1.getName());
        assertThat(save).isFalse();
    }


    @Test
    void saveNonExistsEntry() {
        Boolean save = namespaceDao.save(TestData.newNamespace.getName());
        assertThat(save).isTrue();
    }

    @Test
    void deleteExistsEntry() {
        Boolean delete = namespaceDao.delete(TestData.expectedNamespace3.getName());
        assertThat(delete).isTrue();
    }

    @Test
    void deleteNonExistsEntry() {
        Boolean delete = namespaceDao.delete(TestData.newNamespace.getName());
        assertThat(delete).isFalse();
    }
}
