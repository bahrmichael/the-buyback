package com.thebuyback.eve.service;

import com.thebuyback.eve.domain.ManufacturingOrder;
import com.thebuyback.eve.repository.ManufacturingOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


/**
 * Service Implementation for managing ManufacturingOrder.
 */
@Service
public class ManufacturingOrderService {

    private final Logger log = LoggerFactory.getLogger(ManufacturingOrderService.class);

    private final ManufacturingOrderRepository manufacturingOrderRepository;

    public ManufacturingOrderService(ManufacturingOrderRepository manufacturingOrderRepository) {
        this.manufacturingOrderRepository = manufacturingOrderRepository;
    }

    /**
     * Save a manufacturingOrder.
     *
     * @param manufacturingOrder the entity to save
     * @return the persisted entity
     */
    public ManufacturingOrder save(ManufacturingOrder manufacturingOrder) {
        log.debug("Request to save ManufacturingOrder : {}", manufacturingOrder);
        return manufacturingOrderRepository.save(manufacturingOrder);
    }

    /**
     * Get all the manufacturingOrders.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    public Page<ManufacturingOrder> findAll(Pageable pageable) {
        log.debug("Request to get all ManufacturingOrders");
        return manufacturingOrderRepository.findAll(pageable);
    }

    /**
     * Get one manufacturingOrder by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    public ManufacturingOrder findOne(String id) {
        log.debug("Request to get ManufacturingOrder : {}", id);
        return manufacturingOrderRepository.findOne(id);
    }

    /**
     * Delete the manufacturingOrder by id.
     *
     * @param id the id of the entity
     */
    public void delete(String id) {
        log.debug("Request to delete ManufacturingOrder : {}", id);
        manufacturingOrderRepository.delete(id);
    }
}
