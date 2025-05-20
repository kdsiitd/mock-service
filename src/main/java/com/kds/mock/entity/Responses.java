package com.kds.mock.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.kds.mock.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Responses extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "endpoints_id", nullable = false)
    @JsonBackReference
    private Endpoints endpoints;

    private String method;

    private String contentType;

    private String body;

    public Responses(Endpoints endpoints, String method, String contentType, String body) {
        this.endpoints = endpoints;
        this.method = method;
        this.contentType = contentType;
        this.body = body;
    }
}
