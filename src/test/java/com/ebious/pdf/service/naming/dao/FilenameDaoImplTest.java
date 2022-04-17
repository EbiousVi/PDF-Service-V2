package com.ebious.pdf.service.naming.dao;

import com.ebious.pdf.TestData;
import com.ebious.pdf.domain.entity.Filename;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class FilenameDaoImplTest {

    @Autowired
    FilenameDao filenameDao;

    @Test
    void save() {
        Boolean save = filenameDao.save(TestData.expectedNamespace1.getName(), TestData.expectedNamespace1.getName());
        assertThat(save).isTrue();
    }

    @Test
    void saveNotExistNamespace() {
        assertThatThrownBy(() -> {
            filenameDao.save(TestData.notExistNamespace.getName(), TestData.notExistFilename.getName());
        }).isInstanceOf(DataIntegrityViolationException.class).hasMessageContaining("Referential integrity constraint violation");
    }

    @Test
    void saveAll() {
        Boolean saveAll = filenameDao.saveAll(TestData.expectedNamespaceWithoutFilenames.getName(), TestData.batchFilenamesOk);
        assertThat(saveAll).isTrue();
    }

    @Test
    void deleteTrue() {
        Boolean save = filenameDao.delete(TestData.expectedNamespace2.getName(), TestData.expected_filename1_Namespace2.getName());
        assertThat(save).isTrue();
    }

    @Test
    void deleteFalse() {
        Boolean save = filenameDao.delete(TestData.notExistFilename.getName(), TestData.notExistNamespace.getName());
        assertThat(save).isFalse();
    }
}