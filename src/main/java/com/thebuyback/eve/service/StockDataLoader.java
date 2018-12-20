package com.thebuyback.eve.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.thebuyback.eve.domain.MarketOrder;
import com.thebuyback.eve.domain.Token;
import com.thebuyback.eve.domain.enumeration.Availability;
import com.thebuyback.eve.domain.stock.Doctrine;
import com.thebuyback.eve.domain.stock.Fitting;
import com.thebuyback.eve.domain.stock.FittingItem;
import com.thebuyback.eve.domain.stock.Hub;
import com.thebuyback.eve.domain.stock.StockDoctrine;
import com.thebuyback.eve.domain.stock.StockItem;
import com.thebuyback.eve.domain.stock.TypeStockHistory;
import com.thebuyback.eve.repository.DoctrineRepository;
import com.thebuyback.eve.repository.FittingRepository;
import com.thebuyback.eve.repository.HubRepository;
import com.thebuyback.eve.repository.MarketOrderRepository;
import com.thebuyback.eve.repository.StockDoctrineRepository;
import com.thebuyback.eve.repository.TokenRepository;
import com.thebuyback.eve.repository.TypeStockHistoryRepository;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.github.jhipster.config.JHipsterConstants;

@Component
public class StockDataLoader {

    private final Logger log = LoggerFactory.getLogger(StockDataLoader.class);
    public static final long JITA = 60003760L;
    private static final String BRAVE_MARKET_HUB_CLIENT = "295ff85e960548d080f4ca29f8ba3f87";

    private final TokenRepository tokenRepository;
    private final HubRepository hubRepository;
    private final JsonRequestService requestService;
    private final MarketOrderRepository marketOrderRepository;
    private final TypeStockHistoryRepository stockHistoryRepository;
    private final DoctrineRepository doctrineRepository;
    private final FittingRepository fittingRepository;
    private final TypeService typeService;
    private final StockDoctrineRepository stockDoctrineRepository;
    private final Environment env;

    public StockDataLoader(final TokenRepository tokenRepository,
                           final HubRepository hubRepository,
                           final JsonRequestService requestService,
                           final MarketOrderRepository marketOrderRepository,
                           final TypeStockHistoryRepository stockHistoryRepository,
                           final DoctrineRepository doctrineRepository,
                           final FittingRepository fittingRepository,
                           final TypeService typeService,
                           final StockDoctrineRepository stockDoctrineRepository,
                           final Environment env) {
        this.tokenRepository = tokenRepository;
        this.hubRepository = hubRepository;
        this.requestService = requestService;
        this.marketOrderRepository = marketOrderRepository;
        this.stockHistoryRepository = stockHistoryRepository;
        this.doctrineRepository = doctrineRepository;
        this.fittingRepository = fittingRepository;
        this.typeService = typeService;
        this.stockDoctrineRepository = stockDoctrineRepository;
        this.env = env;
    }

    @Async
    @Scheduled(fixedDelay = 1_200_000L) // 20 minutes
    public void loadAllData() {
        if (env.acceptsProfiles(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT)) {
            return;
        }

        log.info("Loading data for all hubs.");
        Stream.concat(hubRepository.findAll().stream().map(Hub::getId), Stream.of(JITA)).collect(Collectors.toList()).forEach(id -> {
            try {
                loadData(id);
            } catch (UnirestException e) {
                log.error("Couldn't load market orders for {}.", id, e);
            }
        });
        log.info("Calculating stock histories.");
        hubRepository.findAll().forEach(this::doAddStockHistories);
        log.info("Calculating stock doctrines.");
        hubRepository.findAll().forEach(this::calculateDoctrineStocks);
        log.info("Completed stock doctrines.");
    }

    private void calculateDoctrineStocks(final Hub hub) {
        final List<Doctrine> doctrines = doctrineRepository.findByHub(hub);
        final List<Fitting> fittings = getFittingsForDoctrines(doctrines);
        final List<Long> typeIds = fittings.stream().map(Fitting::getItems)
                                           .flatMap(Collection::stream)
                                           .map(FittingItem::getTypeId)
                                           .collect(Collectors.toList());
        final List<MarketOrder> targetOrders = marketOrderRepository
            .findByLocationIdAndTypeIdInAndIsBuyOrderOrderByPriceAsc(hub.getId(), typeIds, false);
        final List<MarketOrder> jitaOrders = marketOrderRepository
            .findByLocationIdAndTypeIdInAndIsBuyOrderOrderByPriceAsc(JITA, typeIds, false);
        stockDoctrineRepository.deleteByHub(hub);
        doctrines.forEach(doctrine -> {
            Availability lowestAvailability = Availability.WELL_PRICED;
            final Map<Long, Long> itemTargetQuantities = new HashMap<>();
            for (final Entry<String, Integer> stringIntegerEntry : doctrine.getFittingsQuantities().entrySet()) {
                final String fittingId = stringIntegerEntry.getKey();
                final Integer fittingQuantity = stringIntegerEntry.getValue();
                addItemQuantities(fittings, fittingId, fittingQuantity, itemTargetQuantities);
            }

            final List<StockItem> stockItems = new ArrayList<>();
            for (Entry<Long, Long> itemQuantity : itemTargetQuantities.entrySet()) {
                final long typeId = itemQuantity.getKey();
                final long stockedQuantity = getStockedQuantity(typeId, targetOrders);
                final long targetQuantity = itemQuantity.getValue();
                final Optional<Double> targetPrice = getBestPrice(typeId, targetOrders);
                final Optional<Double> jitaPrice = getBestPrice(typeId, jitaOrders);
                final Availability availability = getAvailability(stockedQuantity, targetQuantity, targetPrice, jitaPrice);
                if (availability != Availability.WELL_PRICED && lowestAvailability != Availability.MISSING) {
                    // this should reduce to overpriced, and once reduced to missing never be called again
                    lowestAvailability = availability;
                }
                stockItems.add(new StockItem(typeId, typeService.getNameByTypeId(typeId), availability, stockedQuantity, targetQuantity));
            }
            stockDoctrineRepository.save(new StockDoctrine(lowestAvailability, doctrine.getName(), doctrine.getHub(), stockItems));
        });
    }

    private void addItemQuantities(final List<Fitting> fittings, final String fittingId,
                                   final Integer fittingQuantity, final Map<Long, Long> itemQuantities) {
        getFitting(fittingId, fittings).ifPresent(fitting -> fitting.getItems().forEach(item -> {
            if (!itemQuantities.containsKey(item.getTypeId())) {
                itemQuantities.put(item.getTypeId(), 0L);
            }
            itemQuantities.put(item.getTypeId(), itemQuantities.get(item.getTypeId()) + item.getQuantity() * fittingQuantity);
        }));
    }

    private Optional<Double> getBestPrice(final long typeId, final List<MarketOrder> targetOrders) {
        return targetOrders.stream().filter(order -> typeId == order.getTypeId()).findFirst().map(MarketOrder::getPrice);
    }

    private Availability getAvailability(final long stockedQuantity, final long targetQuantity,
                                         final Optional<Double> targetPrice,
                                         final Optional<Double> jitaPrice) {
        if (stockedQuantity < targetQuantity || !targetPrice.isPresent() || !jitaPrice.isPresent()) {
            return Availability.MISSING;
        } else {
            final double overpricedBorder = calcOverpricedBorder(jitaPrice.get());
            if (overpricedBorder < targetPrice.get()) {
                return Availability.OVER_PRICED;
            } else {
                return Availability.WELL_PRICED;
            }
        }
    }

    private static double calcOverpricedBorder(final Double price) {
        return price * (1 + (8 / (Math.log(price) + 3)));
    }

    private long getStockedQuantity(final long typeId, final List<MarketOrder> targetOrders) {
        return targetOrders.stream().filter(order -> typeId == order.getTypeId()).mapToLong(MarketOrder::getVolumeRemain).sum();
    }

    private Optional<Fitting> getFitting(final String fittingId, final List<Fitting> fittings) {
        for (final Fitting fitting : fittings) {
            if (fittingId.equals(fitting.getId())) {
                return Optional.of(fitting);
            }
        }
        return Optional.empty();
    }

    private List<Fitting> getFittingsForDoctrines(final List<Doctrine> doctrines) {
        final List<String> fittingIds = doctrines.stream()
                                              .map(doctrine -> doctrine.getFittingsQuantities().entrySet())
                                              .flatMap(Collection::stream)
                                              .map(Entry::getKey)
                                              .collect(Collectors.toList());
        return fittingRepository.findAllByIdIn(fittingIds);
    }

    private void doAddStockHistories(final Hub hub) {
        final Map<Long, Long> typeIdStocks = new HashMap<>();
        log.debug("Loading market order quantities for {}", hub);
        final Set<Long> doctrineTypeIds = new HashSet<>();
        doctrineRepository.findByHub(hub).forEach(doctrine -> {
            final List<String> fittingIds = doctrine.getFittingsQuantities().entrySet().stream().map(Entry::getKey).collect(Collectors.toList());
            fittingRepository.findAllByIdIn(fittingIds).forEach(fitting -> fitting.getItems().forEach(item -> doctrineTypeIds.add(item.getTypeId())));
        });
        marketOrderRepository.findByLocationIdAndIsBuyOrder(hub.getId(), false)
                             .stream().filter(order -> doctrineTypeIds.contains(order.getTypeId()))
                             .forEach(order -> {
                                 final Long typeId = order.getTypeId();
                                 if (!typeIdStocks.containsKey(typeId)) {
                                     typeIdStocks.put(typeId, 0L);
                                 }
                                 typeIdStocks.put(typeId, typeIdStocks.get(typeId) + order.getVolumeRemain());
                             });
        doctrineTypeIds.forEach(id -> {
            if (!typeIdStocks.containsKey(id)) {
                typeIdStocks.put(id, 0L);
            }
        });
        log.debug("Calculating TypeStockHistories for {}.", hub);
        typeIdStocks.forEach((typeId, quantity) -> {
            final List<MarketOrder> sellOrdersForType = marketOrderRepository
                .findByLocationIdAndIsBuyOrderAndTypeIdOrderByPriceAsc(hub.getId(), false, typeId);
            final Optional<TypeStockHistory> optional = stockHistoryRepository.findByDateAndTypeIdAndHub(
                LocalDate.now(), typeId, hub);
            final TypeStockHistory stockHistory = optional.orElseGet(() -> new TypeStockHistory(typeId, hub));
            if (!sellOrdersForType.isEmpty()) {
                setPrices(sellOrdersForType.get(0).getPrice(), stockHistory);
            }
            setQuantities(quantity, stockHistory);
            stockHistory.setLatestQuantity(quantity);
            stockHistoryRepository.save(stockHistory);
        });
    }

    private void setQuantities(final Long quantity, final TypeStockHistory stockHistory) {
        if (stockHistory.getMinQuantity() != null && stockHistory.getMinQuantity() > quantity) {
            stockHistory.setMinQuantity(quantity);
        } else if (stockHistory.getMaxQuantity() != null && stockHistory.getMaxQuantity() < quantity) {
            stockHistory.setMaxQuantity(quantity);
        }
        if (stockHistory.getMinQuantity() == null) {
            stockHistory.setMinQuantity(quantity);
        }
        if (stockHistory.getMaxQuantity() == null) {
            stockHistory.setMaxQuantity(quantity);
        }
    }

    private void setPrices(final Double price, final TypeStockHistory stockHistory) {
        if (stockHistory.getMinPrice() != null && stockHistory.getMinPrice() > price) {
            stockHistory.setMinPrice(price);
        } else if (stockHistory.getMaxPrice() != null && stockHistory.getMaxPrice() < price) {
            stockHistory.setMaxPrice(price);
        }
        if (stockHistory.getMinPrice() == null) {
            stockHistory.setMinPrice(price);
        }
        if (stockHistory.getMaxPrice() == null) {
            stockHistory.setMaxPrice(price);
        }
    }

    private void loadData(final Long locationId) throws UnirestException {
        final Token token = tokenRepository.findByClientId(BRAVE_MARKET_HUB_CLIENT).get(0);
        String authToken = requestService.getAccessToken(token);
        final List<MarketOrder> orders = getOrders(authToken, locationId);
        if (!orders.isEmpty()) {
            marketOrderRepository.deleteByLocationId(locationId);
            marketOrderRepository.save(orders);
            log.info("Updated {} market orders for {}.", orders.size(), locationId);
        } else {
            log.warn("No data could be loaded for {}.", locationId);
        }
    }

    private List<MarketOrder> getOrders(final String token, final Long target) throws UnirestException {
        final List<MarketOrder> orders = new ArrayList<>();
        int previousCount = 1;
        int pageCount = 1;
        while(previousCount > 0) {
            final JSONArray pagedOrders = getPagedOrders(token, pageCount++, target);
            if (null == pagedOrders) {
                return Collections.emptyList();
            }
            previousCount = pagedOrders.length();
            final List<MarketOrder> ordersForPage = transformPagedOrders(pagedOrders, target);
            orders.addAll(ordersForPage);
        }
        return orders;
    }

    private List<MarketOrder> transformPagedOrders(final JSONArray orderArray, final Long target) {
        final List<MarketOrder> orders = new ArrayList<>();
        for (int i = 0; i < orderArray.length(); i++) {
            final JSONObject jsonObject = orderArray.getJSONObject(i);
            final MarketOrder order = new MarketOrder();

            // the payload one time didn't contain the location id
            if (!jsonObject.has("location_id")) {
                log.warn("A market order did not contain a locationId. {}", order);
                continue;
            }

            order.setLocationId(jsonObject.getLong("location_id"));
            order.setTypeId(jsonObject.getLong("type_id"));

            if (Objects.equals(JITA, target) && !Objects.equals(JITA, order.getLocationId())) {
                continue;
            }

            order.setDuration(jsonObject.getInt("duration"));
            order.setMinVolume(jsonObject.getLong("min_volume"));
            order.setPrice(jsonObject.getDouble("price"));
            order.setBuyOrder(jsonObject.getBoolean("is_buy_order"));
            order.setVolumeTotal(jsonObject.getLong("volume_total"));
            order.setRange(jsonObject.getString("range"));
            order.setIssued(Instant.parse(jsonObject.getString("issued")));
            order.setOrderId(jsonObject.getLong("order_id"));
            order.setVolumeRemain(jsonObject.getLong("volume_remain"));
            order.setTimestamp(Instant.now());

            orders.add(order);
        }
        return orders;
    }

    private static final DateTimeFormatter FMT = new DateTimeFormatterBuilder()
        .appendPattern("EEE, dd MMM yyyy HH:mm:ss zzz")
        .toFormatter()
        .withZone(ZoneOffset.UTC);

    private JSONArray getPagedOrders(final String token, final int page, final long target) throws UnirestException {
        final String url = makeUrl(token, page, target);
        HttpResponse<JsonNode> response = Unirest.get(url).header("User-Agent", "Eve: Rihan Shazih").asJson();

        if (response.getStatus() != 200) {
            log.warn("{} returned status code {}.", url, response.getStatus());
            return null;
        }

        log.debug("Loading page {} for {}", page, target);
        JSONArray array = response.getBody().getArray();
        log.debug("{} page {} contains {} orders", target, page, array.length());
        return array;
    }

    private String makeUrl(final String token, int page, final Long target) {
        final List<Long> hubIds = hubRepository.findAll().stream().map(Hub::getId).collect(Collectors.toList());
        if (hubIds.contains(target)) {
            return "https://esi.evetech.net/v1/markets/structures/" + target
                   + "/?datasource=tranquility"
                   + "&page=" + page
                   + "&token=" + token;
        } else if (Objects.equals(JITA, target)) {
            return "https://esi.evetech.net/v1/markets/10000002/orders/?datasource=tranquility&order_type=all&page=" + page;
        } else {
            throw new IllegalArgumentException("Unknown target: " + target);
        }
    }

    private Instant parseInstant(final String dateString) {
        return FMT.parse(dateString, Instant::from);
    }
}
