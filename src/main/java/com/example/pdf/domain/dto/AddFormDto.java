package com.example.pdf.domain.dto;

public class AddFormDto {
    private String namespace;
    private String filename;

    public AddFormDto() {
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
