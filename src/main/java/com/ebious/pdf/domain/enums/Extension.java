package com.ebious.pdf.domain.enums;

public enum Extension {
    PDF(".pdf"),
    ZIP(".zip"),
    JSON(".json"),
    JPEG(".jpeg");

    public final String name;

    Extension(String name) {
        this.name = name;
    }
}
