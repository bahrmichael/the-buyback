package com.thebuyback.eve.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.codahale.metrics.annotation.Timed;
import com.thebuyback.eve.domain.CapOrder;
import com.thebuyback.eve.domain.enumeration.CapOrderStatus;
import com.thebuyback.eve.repository.CapOrderRepository;
import com.thebuyback.eve.security.AuthoritiesConstants;
import com.thebuyback.eve.security.SecurityUtils;
import com.thebuyback.eve.web.rest.util.HeaderUtil;
import com.thebuyback.eve.web.rest.util.PaginationUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.jhipster.web.util.ResponseUtil;
import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing CapOrder.
 */
@RestController
@RequestMapping("/api")
public class CapOrderResource {

    private final Logger log = LoggerFactory.getLogger(CapOrderResource.class);

    private static final String ENTITY_NAME = "capOrder";

    private final CapOrderRepository capOrderRepository;
    public CapOrderResource(CapOrderRepository capOrderRepository) {
        this.capOrderRepository = capOrderRepository;
    }

    /**
     * POST  /cap-orders : Create a new capOrder.
     *
     * @param capOrder the capOrder to create
     * @return the ResponseEntity with status 201 (Created) and with body the new capOrder, or with status 400 (Bad Request) if the capOrder has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/cap-orders")
    @Timed
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<CapOrder> createCapOrder(@RequestBody CapOrder capOrder) throws URISyntaxException {
        log.debug("REST request to save CapOrder : {}", capOrder);
        if (capOrder.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new capOrder cannot already have an ID")).body(null);
        }

        capOrder.setRecipient(SecurityUtils.getCurrentUserLoginAsString());
        capOrder.setCreated(Instant.now());
        capOrder.setStatus(CapOrderStatus.REQUESTED);

        CapOrder result = capOrderRepository.save(capOrder);
        return ResponseEntity.created(new URI("/api/cap-orders/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /cap-orders : Updates an existing capOrder.
     *
     * @param capOrder the capOrder to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated capOrder,
     * or with status 400 (Bad Request) if the capOrder is not valid,
     * or with status 500 (Internal Server Error) if the capOrder couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/cap-orders")
    @Secured(AuthoritiesConstants.MANAGER)
    public ResponseEntity<CapOrder> updateCapOrder(@RequestBody CapOrder capOrder) throws URISyntaxException {
        log.debug("REST request to update CapOrder : {}", capOrder);
        if (capOrder.getId() == null) {
            return createCapOrder(capOrder);
        }
        CapOrder result = capOrderRepository.save(capOrder);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, capOrder.getId().toString()))
            .body(result);
    }

    /**
     * GET  /cap-orders : get all the capOrders.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of capOrders in body
     */
    @GetMapping("/cap-orders")
    @Secured(AuthoritiesConstants.MANAGER)
    public ResponseEntity<List<CapOrder>> getAllCapOrders(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of CapOrders");
        Page<CapOrder> page = capOrderRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/cap-orders");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /cap-orders/:id : get the "id" capOrder.
     *
     * @param id the id of the capOrder to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the capOrder, or with status 404 (Not Found)
     */
    @GetMapping("/cap-orders/{id}")
    @Secured(AuthoritiesConstants.MANAGER)
    public ResponseEntity<CapOrder> getCapOrder(@PathVariable String id) {
        log.debug("REST request to get CapOrder : {}", id);
        CapOrder capOrder = capOrderRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(capOrder));
    }

    /**
     * DELETE  /cap-orders/:id : delete the "id" capOrder.
     *
     * @param id the id of the capOrder to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/cap-orders/{id}")
    @Secured(AuthoritiesConstants.MANAGER)
    public ResponseEntity<Void> deleteCapOrder(@PathVariable String id) {
        log.debug("REST request to delete CapOrder : {}", id);
        capOrderRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id)).build();
    }
}
