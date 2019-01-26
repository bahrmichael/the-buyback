package com.thebuyback.eve.web.rest;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.thebuyback.eve.domain.CapitalShipOnContract;
import com.thebuyback.eve.domain.CapitalShipStatus;
import com.thebuyback.eve.domain.Contract;
import com.thebuyback.eve.domain.Token;
import com.thebuyback.eve.repository.CapitalShipRepository;
import com.thebuyback.eve.repository.ContractRepository;
import com.thebuyback.eve.repository.TokenRepository;
import com.thebuyback.eve.service.JsonRequestService;
import com.thebuyback.eve.service.TypeService;
import com.thebuyback.eve.web.dto.CapitalShipOnContractDTO;
import com.thebuyback.eve.web.dto.CapitalSoldDTO;
import com.thebuyback.eve.web.dto.ContractDTO;
import static com.thebuyback.eve.security.AuthoritiesConstants.MANAGER;
import static com.thebuyback.eve.service.ContractParser.CONTRACT_PARSER_CLIENT;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contracts")
public class ContractsResource {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public static final long THE_BUYBACK = 98503372L;
    private static final double BUYBACK_PERCENTAGE = 0.9;
    private static final String MAIL_TEMPLATE = "Hi %s,\\n\\nYour contract from %s does not have the correct price.\\n\\nThe contract price should be %s ISK (%s). Please withdraw the contract (if we haven't rejected it yet) and create a new one with the correct price.\\n\\nThe Buyback\\n\\nPLEASE DO NOT REPLY TO THIS MAIL\\nAsk in #the-buyback on Slack";

    private final ContractRepository contractRepository;
    private final JsonRequestService requestService;
    private final TokenRepository tokenRepository;
    private final CapitalShipRepository capitalShipRepository;
    private final TypeService typeService;

    public ContractsResource(final ContractRepository contractRepository,
                             final JsonRequestService requestService,
                             final TokenRepository tokenRepository,
                             final CapitalShipRepository capitalShipRepository,
                             final TypeService typeService) {
        this.contractRepository = contractRepository;
        this.requestService = requestService;
        this.tokenRepository = tokenRepository;
        this.capitalShipRepository = capitalShipRepository;
        this.typeService = typeService;
    }

    @Secured(MANAGER)
    @PostMapping("/buyback/{contractId}/approve/")
    public ResponseEntity approve(@PathVariable Long contractId) {
        Optional<Contract> optional = contractRepository.findById(contractId);
        if (optional.isPresent()) {
            final Contract contract = optional.get();
            contract.setApproved(true);
            contractRepository.save(contract);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Secured(MANAGER)
    @PostMapping("/buyback/{contractId}/decline/")
    public ResponseEntity sendDeclineMail(@PathVariable Long contractId) {
        Optional<Contract> optional = contractRepository.findById(contractId);
        if (optional.isPresent()) {
            Contract contract = optional.get();
            if (contract.isDeclineMailSent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            } else {

                final String mail = formatMail(contract.getClient(), contract.getDateIssued(), contract.getBuyValue() * BUYBACK_PERCENTAGE, contract.getAppraisalLink());

                final Token token = tokenRepository.findByClientId(CONTRACT_PARSER_CLIENT).get(0);
                final String accessToken;
                try {
                    accessToken = requestService.getAccessToken(token);
                } catch (UnirestException e) {
                    log.error("Failed to get an access token.", e);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }

                final Optional<String> jsonNode = requestService.sendMail(contract.getIssuerId(), "Wrong contract price", mail, accessToken);

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

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping("/buyback/pending")
    public ResponseEntity<List<ContractDTO>> getPendingBuybacks() {
        List<ContractDTO> contracts = contractRepository.findAllByStatusAndAssigneeId("outstanding", THE_BUYBACK)
                                                        .stream().map(mapToDTO()).collect(Collectors.toList());
        return ResponseEntity.ok(contracts);
    }

    @GetMapping("/caps")
    public ResponseEntity<List<CapitalShipOnContractDTO>> getPublicCapitals() {
        List<CapitalShipOnContract> publicHulls = capitalShipRepository.findAllByStatus(CapitalShipStatus.PUBLIC_CONTRACT);
        List<CapitalShipOnContractDTO> result = aggregatePublicHulls(publicHulls);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/caps-sold")
    public ResponseEntity<List<CapitalSoldDTO>> getSoldCaps() {
        final List<Contract> finishedCaps = contractRepository.findAllByStatusAndAssigneeId("finished", 0L);
        return ResponseEntity.ok(finishedCaps.stream().map(this::toCapSoldDTO).filter(Objects::nonNull).sorted(this::compareSoldCaps).collect(Collectors.toList()));
    }

    private int compareSoldCaps(final CapitalSoldDTO o1, final CapitalSoldDTO o2) {
        return Math.toIntExact(o1.getDate().until(o2.getDate(), ChronoUnit.SECONDS));
    }

    private CapitalSoldDTO toCapSoldDTO(final Contract contract) {
        final double price = contract.getPrice();
        return contract.getItems().entrySet().stream().findFirst().map(Entry::getKey).map(integer -> new CapitalSoldDTO(contract.getId(), contract.getDateCompleted(), typeService.getNameByTypeId(integer), price)).orElse(null);
    }

    private List<CapitalShipOnContractDTO> aggregatePublicHulls(final List<CapitalShipOnContract> publicHulls) {
        Map<String, Integer> map = new HashMap<>();
        for (CapitalShipOnContract publicHull : publicHulls) {
            final String typeName = publicHull.getTypeName();
            if (!map.containsKey(typeName)) {
                map.put(typeName, 1);
            } else {
                map.put(typeName, map.get(typeName) + 1);
            }
        }
        return map.entrySet().stream().map(entry -> {
            int typeId = 0;
            for (final CapitalShipOnContract hull : publicHulls) {
                if (hull.getTypeName().equals(entry.getKey())) {
                    typeId = hull.getTypeId();
                }
            }
            return new CapitalShipOnContractDTO(entry.getValue(), entry.getKey(), typeId);
        }).collect(Collectors.toList());
    }

    private static Function<Contract, ContractDTO> mapToDTO() {
        return contract -> new ContractDTO(contract.getId(),
                                           contract.getClient(),
                                           contract.getDateIssued(),
                                           contract.getPrice(),
                                           contract.getReward(),
                                           contract.getBuyValue(),
                                           contract.getSellValue(),
                                           contract.isDeclineMailSent(),
                                           contract.isApproved(),
                                           contract.getBuybackPrice(),
                                           contract.getAppraisalLink(),
                                           contract.isAcceptedLocation());
    }
}
