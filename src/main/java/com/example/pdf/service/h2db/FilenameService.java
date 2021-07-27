package com.example.pdf.service.h2db;

import com.example.pdf.domain.entity.Filename;
import com.example.pdf.exception.CustomDBException;
import com.example.pdf.repository.FilenameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FilenameService {
    private final FilenameRepository filenameRepository;

    @Autowired
    public FilenameService(FilenameRepository filenameRepository) {
        this.filenameRepository = filenameRepository;
    }

    public void addFilename(Filename filename) {
        filenameRepository.save(filename);
    }

    public void deleteFilename(String filename, String namespace) throws CustomDBException {
        Filename _filename = filenameRepository.findByNameAndNamespaceName(filename, namespace)
                .orElseThrow(() -> new CustomDBException("Filename = " + filename + " not found at Namespace = " + namespace));
        filenameRepository.delete(_filename);
    }
}
