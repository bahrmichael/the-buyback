package com.thebuyback.eve.web.rest;

import javax.servlet.http.HttpServletRequest;

import com.codahale.metrics.annotation.Timed;
import com.thebuyback.eve.config.AppraisalService;
import com.thebuyback.eve.domain.Appraisal;
import com.thebuyback.eve.domain.AppraisalFailed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * REST controller for executing appraisal requests.
 */
@RestController
@RequestMapping("/api/appraisal")
public class AppraisalResource {

    private final Logger log = LoggerFactory.getLogger(AppraisalResource.class);
    private final AppraisalService appraisalService;

    public AppraisalResource(final AppraisalService appraisalService) {
        this.appraisalService = appraisalService;
    }

    /**
     * POST  / : Execute an appraisal request.
     *
     * @param appraisal the appraisal to request/update
     * @return the executed/updated Appraisal
     */
    @PostMapping
    @Timed
    public ResponseEntity<Appraisal> requestAppraisal(@RequestBody Appraisal appraisal, HttpServletRequest request) {
        log.debug("REST request to perform an appraisal : {}", appraisal);
        log.info("Request from {} {} ", request.getRemoteAddr() , request.getRemoteHost());

        if (appraisal.getAdditionalRaw() == null || appraisal.getAdditionalRaw().isEmpty()) {
            return ResponseEntity.status(400).build();
        }

        appraisal.updateRaw();
        try {
            appraisal = appraisalService.getAppraisalFromNewLineSeparatedRaw(appraisal.getRaw());
        } catch (AppraisalFailed e) {
            log.error("Appraisal failed.", e);
            return ResponseEntity.status(500).build();
        }

        return ResponseEntity.ok().body(appraisal);
    }

}
