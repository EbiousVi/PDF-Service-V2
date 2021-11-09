package com.example.pdf.domain.enums;

public enum ServiceType {
    SPLIT(new DirsAtRoot[]{
            DirsAtRoot.SPLIT, DirsAtRoot.RENDER_IMG, DirsAtRoot.SPLIT_ALL,
            DirsAtRoot.UPLOAD, DirsAtRoot.ZIP
    }),
    MERGE(new DirsAtRoot[]{DirsAtRoot.UPLOAD, DirsAtRoot.RENDER_IMG, DirsAtRoot.MERGE}),

    TEST_CASE(DirsAtRoot.values());

    private final DirsAtRoot[] dirsAtRoot;

    ServiceType(DirsAtRoot[] myArray) {
        this.dirsAtRoot = myArray;
    }

    public DirsAtRoot[] getDirAtRoot() {
        return dirsAtRoot;
    }
}
