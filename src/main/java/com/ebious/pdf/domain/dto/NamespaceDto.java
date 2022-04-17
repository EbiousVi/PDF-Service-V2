package com.ebious.pdf.domain.dto;

import java.util.List;
import java.util.Objects;

public class NamespaceDto {

    private final String namespace;
    private final List<String> filenames;

    public NamespaceDto(String namespace, List<String> filenames) {
        this.namespace = namespace;
        this.filenames = filenames;
    }

    public String getNamespace() {
        return namespace;
    }

    public List<String> getFilenames() {
        return filenames;
    }

    @Override
    public String toString() {
        return "NamespaceDto{" +
                "namespace='" + namespace + '\'' +
                ", filenames=" + filenames +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NamespaceDto that = (NamespaceDto) o;

        if (!Objects.equals(namespace, that.namespace)) return false;
        return Objects.equals(filenames, that.filenames);
    }

    @Override
    public int hashCode() {
        int result = namespace != null ? namespace.hashCode() : 0;
        result = 31 * result + (filenames != null ? filenames.hashCode() : 0);
        return result;
    }
}
