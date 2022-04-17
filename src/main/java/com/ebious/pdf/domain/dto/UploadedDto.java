package com.ebious.pdf.domain.dto;

import java.util.List;

public class UploadedDto {

    private final String token;
    private final List<String> covers;
    private final List<String> filenames;

    public UploadedDto(String token, List<String> covers, List<String> filenames) {
        this.token = token;
        this.covers = covers;
        this.filenames = filenames;
    }

    public String getToken() {
        return token;
    }

    public List<String> getCovers() {
        return covers;
    }

    public List<String> getFilenames() {
        return filenames;
    }

    @Override
    public String toString() {
        return "UploadedDto{" +
                "token='" + token + '\'' +
                ", covers=" + covers +
                ", filenames=" + filenames +
                '}';
    }
}
