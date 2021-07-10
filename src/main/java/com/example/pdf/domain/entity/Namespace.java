package com.example.pdf.domain.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "namespace")
public class Namespace {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "namespace_id")
    private Integer id;

    @Column(name = "namespace_name", unique = true, nullable = false)
    private String namespace;

    @OneToMany(mappedBy = "namespace", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Filename> filenames;

    @Override
    public String toString() {
        return "Namespace{" +
                "id=" + id +
                ", namespace='" + namespace + '\'' +
                ", filenames=" + filenames +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public List<Filename> getFilenames() {
        return filenames;
    }

    public void setFilenames(List<Filename> filenames) {
        this.filenames = filenames;
    }
}
