package com.thebuyback.eve.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.thebuyback.eve.domain.CapConfig;

import com.thebuyback.eve.repository.CapConfigRepository;
import com.thebuyback.eve.web.rest.errors.BadRequestAlertException;
import com.thebuyback.eve.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing CapConfig.
 */
@RestController
@RequestMapping("/api")
public class CapConfigResource {

    private final Logger log = LoggerFactory.getLogger(CapConfigResource.class);

    private static final String ENTITY_NAME = "capConfig";

    private final CapConfigRepository capConfigRepository;

    public CapConfigResource(CapConfigRepository capConfigRepository) {
        this.capConfigRepository = capConfigRepository;
    }

    /**
     * POST  /cap-configs : Create a new capConfig.
     *
     * @param capConfig the capConfig to create
     * @return the ResponseEntity with status 201 (Created) and with body the new capConfig, or with status 400 (Bad Request) if the capConfig has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/cap-configs")
    @Timed
    public ResponseEntity<CapConfig> createCapConfig(@RequestBody CapConfig capConfig) throws URISyntaxException {
        log.debug("REST request to save CapConfig : {}", capConfig);
        if (capConfig.getId() != null) {
            throw new BadRequestAlertException("A new capConfig cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CapConfig result = capConfigRepository.save(capConfig);
        return ResponseEntity.created(new URI("/api/cap-configs/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /cap-configs : Updates an existing capConfig.
     *
     * @param capConfig the capConfig to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated capConfig,
     * or with status 400 (Bad Request) if the capConfig is not valid,
     * or with status 500 (Internal Server Error) if the capConfig couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/cap-configs")
    @Timed
    public ResponseEntity<CapConfig> updateCapConfig(@RequestBody CapConfig capConfig) throws URISyntaxException {
        log.debug("REST request to update CapConfig : {}", capConfig);
        if (capConfig.getId() == null) {
            return createCapConfig(capConfig);
        }
        CapConfig result = capConfigRepository.save(capConfig);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, capConfig.getId().toString()))
            .body(result);
    }

    /**
     * GET  /cap-configs : get all the capConfigs.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of capConfigs in body
     */
    @GetMapping("/cap-configs")
    @Timed
    public List<CapConfig> getAllCapConfigs() {
        log.debug("REST request to get all CapConfigs");
        return capConfigRepository.findAll();
        }

    /**
     * GET  /cap-configs/:id : get the "id" capConfig.
     *
     * @param id the id of the capConfig to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the capConfig, or with status 404 (Not Found)
     */
    @GetMapping("/cap-configs/{id}")
    @Timed
    public ResponseEntity<CapConfig> getCapConfig(@PathVariable String id) {
        log.debug("REST request to get CapConfig : {}", id);
        CapConfig capConfig = capConfigRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(capConfig));
    }

    /**
     * DELETE  /cap-configs/:id : delete the "id" capConfig.
     *
     * @param id the id of the capConfig to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/cap-configs/{id}")
    @Timed
    public ResponseEntity<Void> deleteCapConfig(@PathVariable String id) {
        log.debug("REST request to delete CapConfig : {}", id);
        capConfigRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id)).build();
    }
}
