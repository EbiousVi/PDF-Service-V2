package com.example.pdf.domain.dto;

import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Validated
public class AddFormDto {
    @NotBlank(message = "{namespace.blank}")
    @Size(min = 3, max = 255, message = "{namespace.size}")
    private String namespace;

    @NotBlank(message = "{filename.blank}")
    @Size(min = 3, max = 255, message = "{filename.size}")
    private String filename;

    public AddFormDto() {
    }

    @Override
    public String toString() {
        return "AddFormDto{" +
                "namespace='" + namespace + '\'' +
                ", filename='" + filename + '\'' +
                '}';
    }

    public AddFormDto(@NotBlank(message = "{namespace.blank}")
                      @Size(min = 3, max = 255, message = "{namespace.size}")
                              String namespace,
                      @NotBlank(message = "{filename.blank}")
                      @Size(min = 3, max = 255, message = "{filename.size}")
                              String filename) {
        this.namespace = namespace;
        this.filename = filename;
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
