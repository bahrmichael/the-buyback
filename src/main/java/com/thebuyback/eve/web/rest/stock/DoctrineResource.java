package com.thebuyback.eve.web.rest.stock;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import com.thebuyback.eve.domain.DoctrineItemDetails;
import com.thebuyback.eve.domain.MarketOrder;
import com.thebuyback.eve.domain.stock.Doctrine;
import com.thebuyback.eve.domain.stock.Hub;
import com.thebuyback.eve.domain.stock.StockDoctrine;
import com.thebuyback.eve.domain.stock.TypeStockHistory;
import com.thebuyback.eve.repository.DoctrineRepository;
import com.thebuyback.eve.repository.HubRepository;
import com.thebuyback.eve.repository.MarketOrderRepository;
import com.thebuyback.eve.repository.StockDoctrineRepository;
import com.thebuyback.eve.repository.TypeStockHistoryRepository;
import com.thebuyback.eve.security.AuthoritiesConstants;
import com.thebuyback.eve.service.TypeService;

import static com.thebuyback.eve.service.StockDataLoader.JITA;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stock/doctrines")
public class DoctrineResource {

    private final Logger log = LoggerFactory.getLogger(DoctrineResource.class);

    private final DoctrineRepository repository;
    private final StockDoctrineRepository stockDoctrineRepository;
    private final HubRepository hubRepository;
    private final TypeStockHistoryRepository typeStockHistoryRepository;
    private final MarketOrderRepository marketOrderRepository;
    private final TypeService typeService;

    public DoctrineResource(final DoctrineRepository repository,
                            final StockDoctrineRepository stockDoctrineRepository,
                            final HubRepository hubRepository,
                            final TypeStockHistoryRepository typeStockHistoryRepository,
                            final MarketOrderRepository marketOrderRepository,
                            final TypeService typeService) {
        this.repository = repository;
        this.stockDoctrineRepository = stockDoctrineRepository;
        this.hubRepository = hubRepository;
        this.typeStockHistoryRepository = typeStockHistoryRepository;
        this.marketOrderRepository = marketOrderRepository;
        this.typeService = typeService;
    }

    @PostMapping
    @Secured(AuthoritiesConstants.MANAGER)
    public void postFitting(@RequestBody Doctrine doctrine) {
        repository.save(doctrine);
        log.info("Doctrine {} created.", doctrine.getName());
    }

    @GetMapping
    @Secured(AuthoritiesConstants.MANAGER)
    public ResponseEntity<List<Doctrine>> getDoctrines() {
        return ResponseEntity.ok(repository.findAll());
    }

    @PostMapping("/migrate-hub/{first}/{second}")
    @Secured(AuthoritiesConstants.MANAGER)
    public ResponseEntity migrateDoctrines(@PathVariable Long first, @PathVariable Long second) {
        final Hub firstHub = hubRepository.findById(first).get();
        final Hub secondHub = hubRepository.findById(second).get();

        final List<Doctrine> list = repository.findByHub(firstHub);
        list.forEach(doctrine -> doctrine.setHub(secondHub));
        repository.save(list);

        return ResponseEntity.ok(list);
    }

    @GetMapping("/{hubId}")
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<List<StockDoctrine>> getStock(@PathVariable("hubId") Long hubId) {
        final Optional<Hub> hub = hubRepository.findById(hubId);
        if (hub.isPresent()) {
            return ResponseEntity.ok(stockDoctrineRepository.findByHub(hub.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/item-stock/{systemName}/{typeId}")
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<DoctrineItemDetails> getItemDetails(@PathVariable final String systemName, @PathVariable final int typeId) {
        return new ResponseEntity<>(getDetails(typeId, systemName), HttpStatus.OK);
    }

    @GetMapping("/item-details/{typeName}")
    @Secured(AuthoritiesConstants.MANAGER)
    public ResponseEntity<DoctrineItemDetails> getItemDetailsForManufacturingOrder(@PathVariable final String typeName) {
        final long typeId = typeService.getTypeId(typeName);
        final DoctrineItemDetails details = getDetails(typeId, "GE-8JV");
        return new ResponseEntity<>(stripUnnecessary(details), HttpStatus.OK);
    }

    private DoctrineItemDetails stripUnnecessary(final DoctrineItemDetails details) {
        details.setJitaBuy(null);
        details.setPriceBorder(null);
        details.setStocked(null);
        details.setTypeId(null);
        details.setStockHistory(null);
        return details;
    }

    private DoctrineItemDetails getDetails(final long typeId, final String systemName) {
        final Hub hub = hubRepository.findBySystemNameAndIsPublic(systemName, true);
        final List<TypeStockHistory> history = typeStockHistoryRepository.findByHubAndTypeId(hub, typeId);
        history.sort(Comparator.comparing(TypeStockHistory::getDate));

        final DoctrineItemDetails result = from(history.get(history.size() - 1));
        final List<MarketOrder> targetSell = marketOrderRepository.findByLocationIdAndIsBuyOrderAndTypeIdOrderByPriceAsc(hub.getId(), false, typeId);
        result.setSellPrice(getFirstOrMinusOne(targetSell));
        final List<MarketOrder> jitaBuy = marketOrderRepository.findByLocationIdAndIsBuyOrderAndTypeIdOrderByPriceDesc(JITA, true, typeId);
        result.setJitaBuy(getFirstOrMinusOne(jitaBuy));
        final List<MarketOrder> jitaSell = marketOrderRepository.findByLocationIdAndIsBuyOrderAndTypeIdOrderByPriceAsc(JITA, false, typeId);
        result.setJitaSell(getFirstOrMinusOne(jitaSell));
        result.setPriceBorder(calcOverpricedBorder(result.getJitaSell()));
        result.setStockHistory(history);
        result.setDaysRemaining(calcDaysRemaining(history, history.get(history.size() - 1).getLatestQuantity().intValue()));

        return result;
    }

    private Integer calcDaysRemaining(final List<TypeStockHistory> history, final int latestQuantity) {
        int totalChange = 0;
        int days = 0;
        // start at the first because we can't check if the first was a huge stock day
        for (int i = 1; i < history.size(); i++) {
            if (history.get(i).getMaxQuantity() > history.get(i - 1).getMaxQuantity()) {
                // skip days that had a positive stock
                continue;
            } else {
                days++;
                totalChange += history.get(i).getMaxQuantity() - history.get(i).getMinQuantity();
            }
        }
        if (days == 0) {
            return -1;
        }
        final int changePerDay = totalChange / days;
        if (changePerDay == 0) {
            // will be turned into N/A in the frontend
            return -1;
        }
        return latestQuantity / changePerDay;
    }

    private DoctrineItemDetails from(final TypeStockHistory item) {
        final DoctrineItemDetails details = new DoctrineItemDetails();
        details.setTypeId(item.getTypeId());
        details.setTypeName(typeService.getNameByTypeId(item.getTypeId()));
        details.setStocked(item.getLatestQuantity());
        return details;
    }

    private double calcOverpricedBorder(final Double price) {
        return price * (1 + (8 / (Math.log(price) + 3)));
    }

    private Double getFirstOrMinusOne(final List<MarketOrder> orders) {
        return orders.isEmpty() ? -1.0 : orders.get(0).getPrice();
    }
}
