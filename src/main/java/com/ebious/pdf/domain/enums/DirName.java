package com.ebious.pdf.domain.enums;

public enum DirName {
    UPLOAD("upload"),
    RENDER("render"),
    ZIP("zip"),
    SPLIT("split"),
    SPLIT_ALL("split_all"),
    MERGE("merge");

    public final String name;

    DirName(String name) {
        this.name = name;
    }
}
