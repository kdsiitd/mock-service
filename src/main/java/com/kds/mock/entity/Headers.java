package com.kds.mock.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.kds.mock.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "HTTP header configuration for mock endpoints")
public class Headers extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "endpoints_id", nullable = false)
    @JsonBackReference
    @Schema(description = "Associated endpoint configuration", hidden = true)
    private Endpoints endpoints;

    @Schema(description = "Header name", example = "Content-Type")
    private String name;
    
    @Schema(description = "Header value", example = "application/json")
    private String value;

    public Headers(Endpoints endpoints, String name, String value) {
        this.endpoints = endpoints;
        this.name = name;
        this.value = value;
    }
}
