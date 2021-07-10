package com.example.pdf.domain.enums;

public enum Extension {
    PDF(".pdf"),
    ZIP(".zip"),
    JPEG(".jpeg");

    public final String name;

    Extension(String name) {
        this.name = name;
    }
}
