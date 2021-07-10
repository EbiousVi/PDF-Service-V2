package com.example.pdf.domain.enums;

public enum ServiceType {
    SPLIT(new DirsAtRoot[]{
            DirsAtRoot.SPLITTED, DirsAtRoot.RENDER_IMG, DirsAtRoot.SPLIT_ALL,
            DirsAtRoot.UPLOADED, DirsAtRoot.ZIP
    }),
    MERGE(new DirsAtRoot[]{DirsAtRoot.UPLOADED, DirsAtRoot.RENDER_IMG, DirsAtRoot.MERGED});

    private final DirsAtRoot[] dirsAtRoot;

    ServiceType(DirsAtRoot[] myArray) {
        this.dirsAtRoot = myArray;
    }

    public DirsAtRoot[] getDirAtRoot() {
        return dirsAtRoot;
    }
}
