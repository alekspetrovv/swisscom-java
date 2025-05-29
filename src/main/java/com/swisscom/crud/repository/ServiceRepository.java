package com.swisscom.crud.repository;

import com.swisscom.crud.model.Service;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends MongoRepository<Service, String> {
    @Query(value = "{}", fields = "{ 'resources': 0 }")
    List<Service> findAllServices();

    @Query(value = "{ '_id': ?0 }", fields = "{ 'resources': 0 }")
    Optional<Service> findByServiceId(String id);

}
