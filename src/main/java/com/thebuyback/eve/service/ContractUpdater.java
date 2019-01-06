package com.thebuyback.eve.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static java.util.Arrays.asList;

import com.thebuyback.eve.config.AppraisalService;
import com.thebuyback.eve.domain.Appraisal;
import com.thebuyback.eve.domain.AppraisalFailed;
import com.thebuyback.eve.domain.Contract;
import com.thebuyback.eve.repository.ContractRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ContractUpdater {

    private final Logger log = LoggerFactory.getLogger(getClass());

    static final Set<Long> ORE_TYPE_IDS = new HashSet<>(
        asList(34L, 35L, 36L, 37L, 38L, 39L, 40L, 11399L, 28367L, 28368L, 28385L, 28386L, 28387L, 28388L, 28389L,
               28390L, 28391L, 28392L, 28393L, 28394L, 28395L, 28396L, 28397L, 28398L, 28399L, 28400L, 28401L, 28402L,
               28403L, 28404L, 28405L, 28406L, 28407L, 28408L, 28409L, 28410L, 28411L, 28412L, 28413L, 28414L, 28415L,
               28416L, 28417L, 28418L, 28419L, 28420L, 28421L, 28422L, 28423L, 28424L, 28425L, 28426L, 28427L, 28428L,
               28429L, 28430L, 28431L, 28432L, 28433L, 28434L, 28435L, 28436L, 28437L, 28438L, 28439L, 28440L, 28441L,
               28442L, 28443L, 28444L, 28448L, 28449L, 28450L, 28451L, 28452L, 28453L, 28454L, 28455L, 28456L, 28457L,
               28458L, 28459L, 28460L, 28461L, 28462L, 28463L, 28464L, 28465L, 28466L, 28467L, 28468L, 28469L, 28470L,
               28471L, 28472L, 28473L, 28474L, 28475L, 28476L, 28477L, 28478L, 28479L, 28480L, 28481L, 28482L, 28483L,
               28484L, 28485L, 28486L, 28487L, 28488L, 28489L, 28490L, 28491L, 28492L, 28493L, 28494L, 28495L, 28496L,
               28497L, 28498L, 28499L, 28500L, 28501L, 28502L, 28503L, 28504L, 28505L));

    private final ContractRepository contractRepository;
    private final AppraisalService appraisalService;

    public ContractUpdater(final ContractRepository contractRepository,
                           final AppraisalService appraisalService) {
        this.contractRepository = contractRepository;
        this.appraisalService = appraisalService;
    }

    @Async
    @Scheduled(fixedDelay = 120000L)
    public void loadOrePrices() {
        log.info("Started updating contracts.");
        final List<Contract> contracts = contractRepository.findTop50ByOreValueNullAndStatusAndAppraisalLinkNotNull("finished");
        for (Contract contract : contracts) {
            call(contract);
        }
        log.info("Finished updating contracts.");
    }

    public void call(final Contract contract) {
        try {
            final Appraisal appraisal = appraisalService.getFromAppraisalLink(contract.getAppraisalLink());

            double oreValue = appraisal.getItems()
                                       .stream().filter(item -> ORE_TYPE_IDS.contains(item.getTypeID()))
                                       .mapToDouble(item -> item.getJitaBuyPerUnit() * item.getQuantity() * item.getRate())
                                       .sum();
            double otherValue = appraisal.getItems()
                                         .stream().filter(item -> !ORE_TYPE_IDS.contains(item.getTypeID()))
                                         .mapToDouble(item -> item.getJitaBuyPerUnit() * item.getQuantity() * item.getRate())
                                         .sum();

            contract.setOreValue(oreValue);
            contract.setOtherValue(otherValue);

            contractRepository.save(contract);
            log.info("Completed updating contract {}", contract.getId());
        } catch (AppraisalFailed e) {
            log.error("Failed to load contract {}", contract.getAppraisalLink(), e);

            if (e.getStatusText().equals("Not Found")) {
                contract.setAppraisalLink(null);
                contractRepository.save(contract);
            }
        }
    }
}
