package com.kds.mock.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.kds.mock.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Schema(description = "Response configuration for mock endpoints")
public class Responses extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "endpoints_id", nullable = false)
    @JsonBackReference
    @Schema(description = "Associated endpoint configuration", hidden = true)
    private Endpoints endpoints;

    @Schema(description = "HTTP method", example = "GET")
    private String method;

    @Schema(description = "Content type of the response", example = "application/json")
    private String contentType;

    @Schema(description = "Response body content", example = "{\"message\": \"Hello World\"}")
    private String body;

    public Responses(Endpoints endpoints, String method, String contentType, String body) {
        this.endpoints = endpoints;
        this.method = method;
        this.contentType = contentType;
        this.body = body;
    }
}
