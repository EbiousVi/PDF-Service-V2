package com.ebious.pdf.domain.entity;

import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Validated
public class Filename {

    @NotBlank
    private final Long id;

    @Length(min = 3, max = 255)
    @NotBlank
    private final String name;

    public Filename(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Filename{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
