package com.example.pdf.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "namespace")
@Validated
public class Namespace {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "namespace_id")
    private Integer id;

    @Column(name = "namespace_name", unique = true, nullable = false)
    @Length(min = 3, max = 255)
    @NotBlank
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "namespace", orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Filename> filenames;

    public Namespace(@Size(min = 3, max = 255) String name) {
        this.name = name;
    }

    public Namespace() {
    }

    @Override
    public String toString() {
        return "Namespace{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
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

    public void setName(String namespace) {
        this.name = namespace;
    }

    public List<Filename> getFilenames() {
        return filenames;
    }

    public void setFilenames(List<Filename> filenames) {
        this.filenames = filenames;
    }
}
