package com.example.pdf.domain.entity;

import javax.persistence.*;

@Entity
@Table(name = "filenames",
        uniqueConstraints = {
                @UniqueConstraint(name = "UniqueFilenameAndNamespace", columnNames = {"filename_name", "namespace_id"})
        })
public class Filename {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "filename_id")
    private Integer id;

    @Column(name = "filename_name", nullable = false)
    private String filename;

    @ManyToOne
    @JoinColumn(name = "namespace_id")
    private Namespace namespace;

    @Override
    public String toString() {
        return "Filename{" +
                "id=" + id +
                ", filenames='" + filename + '\'' +
                ", namespace=" + namespace +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Namespace getNamespace() {
        return namespace;
    }

    public void setNamespace(Namespace namespace) {
        this.namespace = namespace;
    }
}
