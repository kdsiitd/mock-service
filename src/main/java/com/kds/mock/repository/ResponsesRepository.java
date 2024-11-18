package com.kds.mock.repository;

import com.kds.mock.entity.Responses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponsesRepository extends JpaRepository<Responses, Long> {
    Responses findResponseByEndpointsIdAndMethod(Long endpointsId, String method);
}
