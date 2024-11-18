package com.kds.mock.repository;

import com.kds.mock.entity.Endpoints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EndpointsRepository extends JpaRepository<Endpoints, Long> {
    Endpoints findEndpointByPath(String path);
}
