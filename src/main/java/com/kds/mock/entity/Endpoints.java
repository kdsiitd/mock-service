package com.kds.mock.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kds.mock.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
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
@Schema(description = "Mock endpoint configuration entity")
public class Endpoints extends BaseEntity {
    
    @Schema(description = "URL path of the mock endpoint", example = "/api/users")
    private String path;

    /**
     * should have values from HTTPStatus.value()
     */
    @Schema(description = "HTTP status code to return", example = "200")
    private int statusCode;

    @Schema(description = "Description of the endpoint", example = "Mock users endpoint")
    private String description;

    @Column(columnDefinition = "TEXT")
    @Schema(description = "Load testing configuration for this endpoint (JSON format)")
    @JsonIgnore
    private String loadTestConfig;

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
