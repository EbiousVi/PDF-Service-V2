package com.ebious.pdf.service.naming.dao;

import java.util.List;

public interface FilenameDao {

    Boolean save(String namespace, String filename);

    Boolean saveAll(String namespace, List<String> filenames);

    Boolean delete(String namespace, String filename);
}
