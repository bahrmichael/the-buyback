package com.thebuyback.eve.repository;

import com.thebuyback.eve.domain.CapConfig;
import org.springframework.stereotype.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the CapConfig entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CapConfigRepository extends MongoRepository<CapConfig, String> {

}
