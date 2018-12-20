package com.thebuyback.eve.repository;

import java.util.List;
import java.util.Optional;

import com.thebuyback.eve.domain.Contract;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Doctrine entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ContractRepository extends MongoRepository<Contract,String> {
    List<Contract> findAllByStatusAndAssigneeIdAndIssuerCorporationId(String status, long assigneeId, long issuerCorporationId);
    List<Contract> findAllByStatusAndAssigneeId(String status, long assigneeId);
    Optional<Contract> findById(long contractId);
    List<Contract> findTop50ByOreValueNullAndStatusAndAppraisalLinkNotNull(String status);
}
