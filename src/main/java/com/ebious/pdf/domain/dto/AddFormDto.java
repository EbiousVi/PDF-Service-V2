package com.ebious.pdf.domain.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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

    public AddFormDto(String namespace, String filename) {
        this.namespace = namespace;
        this.filename = filename;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getFilename() {
        return filename;
    }

    @Override
    public String toString() {
        return "AddFormDto{" +
                "namespace='" + namespace + '\'' +
                ", filename='" + filename + '\'' +
                '}';
    }
}
