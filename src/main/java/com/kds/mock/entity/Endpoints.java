package com.kds.mock.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.kds.mock.entity.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Endpoints extends BaseEntity {
    private String path;

    /**
     * should have values from HTTPStatus.value()
     */
    private int statusCode;

    private String description;

    @OneToMany(mappedBy = "endpoints", cascade = jakarta.persistence.CascadeType.ALL)
    @JsonManagedReference
    private List<Headers> headers = new ArrayList<>();

    @OneToMany(mappedBy = "endpoints", cascade = jakarta.persistence.CascadeType.ALL)
    @JsonManagedReference
    private List<Responses> responses = new ArrayList<>();

    public Endpoints(String path, int statusCode, String description) {
        this.path = path;
        this.statusCode = statusCode;
        this.description = description;
    }
}
