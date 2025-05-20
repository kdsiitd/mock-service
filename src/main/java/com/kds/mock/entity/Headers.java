package com.kds.mock.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.kds.mock.entity.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Headers extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "endpoints_id", nullable = false)
    @JsonBackReference
    private Endpoints endpoints;

    private String name;
    private String value;

    public Headers(Endpoints endpoints, String name, String value) {
        this.endpoints = endpoints;
        this.name = name;
        this.value = value;
    }
}
