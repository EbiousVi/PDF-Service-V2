package com.example.pdf.domain.enums;

public enum Filename {
    SPLITTED_PAGES("splitted_pages_"),
    SINGLE_PAGE("single_page_"),
    ZIP("zip"),
    IMG_PAGE("img_page_"),
    IMG_COVER("img_cover_"),
    MERGED("merged");

    public final String name;

    Filename(String name) {
        this.name = name;
    }
}
