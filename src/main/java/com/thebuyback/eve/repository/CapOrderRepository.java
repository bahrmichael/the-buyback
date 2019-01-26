package com.thebuyback.eve.repository;

import com.thebuyback.eve.domain.CapOrder;
import org.springframework.stereotype.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the CapOrder entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CapOrderRepository extends MongoRepository<CapOrder, String> {

}
