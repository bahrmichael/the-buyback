package com.thebuyback.eve.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.thebuyback.eve.domain.TypeBuybackRate;

import com.thebuyback.eve.repository.TypeBuybackRateRepository;
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
 * REST controller for managing TypeBuybackRate.
 */
@RestController
@RequestMapping("/api")
public class TypeBuybackRateResource {

    private final Logger log = LoggerFactory.getLogger(TypeBuybackRateResource.class);

    private static final String ENTITY_NAME = "typeBuybackRate";

    private final TypeBuybackRateRepository typeBuybackRateRepository;

    public TypeBuybackRateResource(TypeBuybackRateRepository typeBuybackRateRepository) {
        this.typeBuybackRateRepository = typeBuybackRateRepository;
    }

    /**
     * POST  /type-buyback-rates : Create a new typeBuybackRate.
     *
     * @param typeBuybackRate the typeBuybackRate to create
     * @return the ResponseEntity with status 201 (Created) and with body the new typeBuybackRate, or with status 400 (Bad Request) if the typeBuybackRate has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/type-buyback-rates")
    @Timed
    public ResponseEntity<TypeBuybackRate> createTypeBuybackRate(@RequestBody TypeBuybackRate typeBuybackRate) throws URISyntaxException {
        log.debug("REST request to save TypeBuybackRate : {}", typeBuybackRate);
        if (typeBuybackRate.getId() != null) {
            throw new BadRequestAlertException("A new typeBuybackRate cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TypeBuybackRate result = typeBuybackRateRepository.save(typeBuybackRate);
        return ResponseEntity.created(new URI("/api/type-buyback-rates/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /type-buyback-rates : Updates an existing typeBuybackRate.
     *
     * @param typeBuybackRate the typeBuybackRate to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated typeBuybackRate,
     * or with status 400 (Bad Request) if the typeBuybackRate is not valid,
     * or with status 500 (Internal Server Error) if the typeBuybackRate couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/type-buyback-rates")
    @Timed
    public ResponseEntity<TypeBuybackRate> updateTypeBuybackRate(@RequestBody TypeBuybackRate typeBuybackRate) throws URISyntaxException {
        log.debug("REST request to update TypeBuybackRate : {}", typeBuybackRate);
        if (typeBuybackRate.getId() == null) {
            return createTypeBuybackRate(typeBuybackRate);
        }
        TypeBuybackRate result = typeBuybackRateRepository.save(typeBuybackRate);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, typeBuybackRate.getId().toString()))
            .body(result);
    }

    /**
     * GET  /type-buyback-rates : get all the typeBuybackRates.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of typeBuybackRates in body
     */
    @GetMapping("/type-buyback-rates")
    @Timed
    public ResponseEntity<List<TypeBuybackRate>> getAllTypeBuybackRates(Pageable pageable) {
        log.debug("REST request to get a page of TypeBuybackRates");
        Page<TypeBuybackRate> page = typeBuybackRateRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/type-buyback-rates");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /type-buyback-rates/:id : get the "id" typeBuybackRate.
     *
     * @param id the id of the typeBuybackRate to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the typeBuybackRate, or with status 404 (Not Found)
     */
    @GetMapping("/type-buyback-rates/{id}")
    @Timed
    public ResponseEntity<TypeBuybackRate> getTypeBuybackRate(@PathVariable String id) {
        log.debug("REST request to get TypeBuybackRate : {}", id);
        TypeBuybackRate typeBuybackRate = typeBuybackRateRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(typeBuybackRate));
    }

    /**
     * DELETE  /type-buyback-rates/:id : delete the "id" typeBuybackRate.
     *
     * @param id the id of the typeBuybackRate to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/type-buyback-rates/{id}")
    @Timed
    public ResponseEntity<Void> deleteTypeBuybackRate(@PathVariable String id) {
        log.debug("REST request to delete TypeBuybackRate : {}", id);
        typeBuybackRateRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id)).build();
    }
}
