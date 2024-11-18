package com.kds.mock.entity;

import com.kds.mock.entity.base.BaseEntity;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Endpoints extends BaseEntity {
    private String path;

    /**
     * should have values from HTTPStatus.value()
     */
    private int statusCode;

    private String description;

}
