package com.thebuyback.eve.repository;

import com.thebuyback.eve.domain.TypeBuybackRate;
import org.springframework.stereotype.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the TypeBuybackRate entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TypeBuybackRateRepository extends MongoRepository<TypeBuybackRate, String> {

}
