package com.kds.mock.repository;

import com.kds.mock.entity.Headers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HeadersRepository extends JpaRepository<Headers, Long> {
    List<Headers> findAllByEndpointsId(Long endpointsId);

}
