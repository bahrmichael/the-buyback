package com.thebuyback.eve.repository;

import com.thebuyback.eve.domain.ManufacturingOrder;
import org.springframework.stereotype.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the ManufacturingOrder entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ManufacturingOrderRepository extends MongoRepository<ManufacturingOrder, String> {

}
