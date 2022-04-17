package com.ebious.pdf.domain.enums;

public enum Prefix {
    PAGES("pages_"),
    PAGE("page_"),
    ZIP("archive"),
    PAGE_COVER("page_cover_"),
    FILE_COVER("file_cover_"),
    UPLOADED("uploaded"),
    MERGED("merged");

    public final String name;

    Prefix(String name) {
        this.name = name;
    }
}
