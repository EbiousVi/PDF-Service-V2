package com.example.pdf.domain.enums;

public enum Filename {
    SPLITTED_PAGES("splitted_pages_"),
    PAGE("page_"),
    ZIP("zip"),
    RENDER_PAGE("render_page_"),
    MERGED("merged");

    public final String name;

    Filename(String name) {
        this.name = name;
    }
}
