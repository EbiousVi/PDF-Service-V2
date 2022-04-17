package com.ebious.pdf.domain.entity;

import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Validated
public class Namespace {

    @NotBlank
    private Long id;

    @Length(min = 3, max = 255)
    @NotBlank
    private String name;

    private List<String> filenames = new ArrayList<>();

    public Namespace() {
    }

    public Namespace(@Size(min = 3, max = 255) String name) {
        this.name = name;
    }

    public Namespace(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Namespace(String name, List<String> filenames) {
        this.name = name;
        this.filenames = filenames;
    }

    public List<String> getFilenames() {
        return filenames;
    }

    public void setFilenames(List<String> filenames) {
        this.filenames = filenames;
    }

    public void addFilename(String filename) {
        this.filenames.add(filename);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Namespace{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", filenames=" + filenames +
                '}';
    }
}
