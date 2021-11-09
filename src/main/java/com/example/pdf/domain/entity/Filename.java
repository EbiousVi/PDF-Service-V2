package com.example.pdf.domain.entity;

import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "filenames",
        uniqueConstraints = {
                @UniqueConstraint(name = "UniqueFilenameAndNamespace", columnNames = {"filename_name", "namespace_id"})
        })
@Validated
public class Filename {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "filename_id")
    private Integer id;

    @Column(name = "filename_name", nullable = false)
    @Length(min = 3, max = 255)
    @NotBlank
    private String name;

    @ManyToOne
    @JoinColumn(name = "namespace_id")
    @NotNull
    private Namespace namespace;

    @Override
    public String toString() {
        return "Filename{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", namespace=" + namespace +
                '}';
    }

    public Filename(@Length(min = 3, max = 255) @NotBlank String name, @NotNull Namespace namespace) {
        this.name = name;
        this.namespace = namespace;
    }

    public Filename() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String filename) {
        this.name = filename;
    }

    public Namespace getNamespace() {
        return namespace;
    }

    public void setNamespace(Namespace namespace) {
        this.namespace = namespace;
    }
}
