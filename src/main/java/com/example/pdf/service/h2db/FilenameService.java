package com.example.pdf.service.h2db;

import com.example.pdf.domain.entity.Filename;
import com.example.pdf.domain.entity.Namespace;
import com.example.pdf.repository.FilenameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FilenameService {
    private final FilenameRepository filenameRepository;

    @Autowired
    public FilenameService(FilenameRepository filenameRepository) {
        this.filenameRepository = filenameRepository;
    }

    public List<Filename> getFilenamesByNamespace(Namespace namespace) {
        return filenameRepository.findAllByNamespace(namespace);
    }

    public void addFilename(Filename filename) {
        filenameRepository.save(filename);
    }

    public void deleteFilenameByName(String name) {
        Filename filename = filenameRepository.findByFilename(name);
        filenameRepository.delete(filename);
    }
}
