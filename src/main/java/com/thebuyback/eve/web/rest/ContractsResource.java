package com.thebuyback.eve.web.rest;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.thebuyback.eve.domain.Contract;
import com.thebuyback.eve.domain.Token;
import com.thebuyback.eve.repository.ContractRepository;
import com.thebuyback.eve.repository.TokenRepository;
import com.thebuyback.eve.service.JsonRequestService;

import static com.thebuyback.eve.service.ContractParser.PARSER_CLIENT;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ContractsResource
 *
 * Created on 08.11.2017
 *
 * Copyright (C) 2017 Volkswagen AG, All rights reserved.
 */
@RestController
@RequestMapping("/api/contracts")
@PreAuthorize("hasRole('ROLE_MANAGER')")
public class ContractsResource {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final long THE_BUYBACK = 98503372L;
    private static final double BUYBACK_PERCENTAGE = 0.9;
    private static final String MAIL_TEMPLATE = "Hi %s,\\n\\nYour contract from %s does not have the correct price (90%% Jita buy).\\n\\nThe contract price should be %s ISK (%s). Please withdraw the contract (if we haven't rejected it yet) and create a new one with the correct price.\\n\\nThe Buyback\\n\\nPLEASE DO NOT REPLY TO THIS MAIL\\nContact Avend Avalhar, Algorthan Gaterau or Rihan Shazih on Slack";
    private final ContractRepository contractRepository;
    private final JsonRequestService requestService;
    private final TokenRepository tokenRepository;

    public ContractsResource(final ContractRepository contractRepository,
                             final JsonRequestService requestService,
                             final TokenRepository tokenRepository) {
        this.contractRepository = contractRepository;
        this.requestService = requestService;
        this.tokenRepository = tokenRepository;
    }

    @PostMapping("/buyback/{contractId}/decline/")
    public ResponseEntity sendDeclineMail(@PathVariable Long contractId) {
        Optional<Contract> optional = contractRepository.findById(contractId);
        if (optional.isPresent()) {
            Contract contract = optional.get();
            if (contract.isDeclineMailSent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            } else {

                final String mail = formatMail(contract.getClient(), contract.getDateIssued(), contract.getBuyValue() * BUYBACK_PERCENTAGE, contract.getAppraisalLink());

                final Token token = tokenRepository.findByClientId(PARSER_CLIENT).get(0);
                final String accessToken;
                try {
                    accessToken = requestService.getAccessToken(token);
                } catch (UnirestException e) {
                    log.error("Failed to get an access token.", e);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }

                final Optional<String> jsonNode = requestService.sendMail(contract.getIssuerId(), mail, accessToken);

                if (!jsonNode.isPresent()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                }

                contract.setDeclineMailSent(true);
                contractRepository.save(contract);

                return ResponseEntity.ok().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    String formatMail(final String client, final Instant dateIssued, final double correctPrice, final String appraisalLink) {

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        formatter.setDecimalFormatSymbols(symbols);

        DateTimeFormatter dateFormatter =
        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                         .withLocale( Locale.GERMAN )
                         .withZone(ZoneId.systemDefault());

        return String.format(MAIL_TEMPLATE, client, dateFormatter.format(dateIssued), formatter.format(correctPrice), appraisalLink);
    }

    @GetMapping("/buyback/pending")
    public ResponseEntity<List<ContractDTO>> getPendingBuybacks() {
        List<ContractDTO> contracts = contractRepository.findAllByStatusAndAssigneeId("outstanding", THE_BUYBACK)
                                                          .stream().map(mapToDTO()).collect(Collectors.toList());
        return ResponseEntity.ok(contracts);
    }

    private static Function<Contract, ContractDTO> mapToDTO() {
        return contract -> new ContractDTO(contract.getId(),
                                           contract.getClient(),
                                           contract.getDateIssued(),
                                           contract.getPrice(),
                                           contract.getBuyValue(),
                                           contract.isDeclineMailSent());
    }
}