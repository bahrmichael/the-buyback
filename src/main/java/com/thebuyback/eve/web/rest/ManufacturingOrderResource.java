package com.thebuyback.eve.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.thebuyback.eve.domain.ManufacturingOrder;
import com.thebuyback.eve.service.ManufacturingOrderService;
import com.thebuyback.eve.web.rest.errors.BadRequestAlertException;
import com.thebuyback.eve.web.rest.util.HeaderUtil;
import com.thebuyback.eve.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing ManufacturingOrder.
 */
@RestController
@RequestMapping("/api")
public class ManufacturingOrderResource {

    private final Logger log = LoggerFactory.getLogger(ManufacturingOrderResource.class);

    private static final String ENTITY_NAME = "manufacturingOrder";

    private final ManufacturingOrderService manufacturingOrderService;

    public ManufacturingOrderResource(ManufacturingOrderService manufacturingOrderService) {
        this.manufacturingOrderService = manufacturingOrderService;
    }

    /**
     * POST  /manufacturing-orders : Create a new manufacturingOrder.
     *
     * @param manufacturingOrder the manufacturingOrder to create
     * @return the ResponseEntity with status 201 (Created) and with body the new manufacturingOrder, or with status 400 (Bad Request) if the manufacturingOrder has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/manufacturing-orders")
    @Timed
    public ResponseEntity<ManufacturingOrder> createManufacturingOrder(@RequestBody ManufacturingOrder manufacturingOrder) throws URISyntaxException {
        log.debug("REST request to save ManufacturingOrder : {}", manufacturingOrder);
        if (manufacturingOrder.getId() != null) {
            throw new BadRequestAlertException("A new manufacturingOrder cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ManufacturingOrder result = manufacturingOrderService.save(manufacturingOrder);
        return ResponseEntity.created(new URI("/api/manufacturing-orders/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /manufacturing-orders : Updates an existing manufacturingOrder.
     *
     * @param manufacturingOrder the manufacturingOrder to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated manufacturingOrder,
     * or with status 400 (Bad Request) if the manufacturingOrder is not valid,
     * or with status 500 (Internal Server Error) if the manufacturingOrder couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/manufacturing-orders")
    @Timed
    public ResponseEntity<ManufacturingOrder> updateManufacturingOrder(@RequestBody ManufacturingOrder manufacturingOrder) throws URISyntaxException {
        log.debug("REST request to update ManufacturingOrder : {}", manufacturingOrder);
        if (manufacturingOrder.getId() == null) {
            return createManufacturingOrder(manufacturingOrder);
        }
        ManufacturingOrder result = manufacturingOrderService.save(manufacturingOrder);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, manufacturingOrder.getId().toString()))
            .body(result);
    }

    /**
     * GET  /manufacturing-orders : get all the manufacturingOrders.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of manufacturingOrders in body
     */
    @GetMapping("/manufacturing-orders")
    @Timed
    public ResponseEntity<List<ManufacturingOrder>> getAllManufacturingOrders(Pageable pageable) {
        log.debug("REST request to get a page of ManufacturingOrders");
        Page<ManufacturingOrder> page = manufacturingOrderService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/manufacturing-orders");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /manufacturing-orders/:id : get the "id" manufacturingOrder.
     *
     * @param id the id of the manufacturingOrder to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the manufacturingOrder, or with status 404 (Not Found)
     */
    @GetMapping("/manufacturing-orders/{id}")
    @Timed
    public ResponseEntity<ManufacturingOrder> getManufacturingOrder(@PathVariable String id) {
        log.debug("REST request to get ManufacturingOrder : {}", id);
        ManufacturingOrder manufacturingOrder = manufacturingOrderService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(manufacturingOrder));
    }

    /**
     * DELETE  /manufacturing-orders/:id : delete the "id" manufacturingOrder.
     *
     * @param id the id of the manufacturingOrder to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/manufacturing-orders/{id}")
    @Timed
    public ResponseEntity<Void> deleteManufacturingOrder(@PathVariable String id) {
        log.debug("REST request to delete ManufacturingOrder : {}", id);
        manufacturingOrderService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id)).build();
    }
}
