package com.thebuyback.eve.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.thebuyback.eve.config.AppraisalService;
import com.thebuyback.eve.domain.Appraisal;
import com.thebuyback.eve.domain.AppraisalFailed;
import com.thebuyback.eve.domain.Asset;
import com.thebuyback.eve.domain.ItemWithQuantity;
import com.thebuyback.eve.domain.Token;
import com.thebuyback.eve.repository.NetWorthHistoryRepository;
import com.thebuyback.eve.repository.AssetRepository;
import com.thebuyback.eve.repository.TokenRepository;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Service;

import io.github.jhipster.config.JHipsterConstants;

@Service
public class AssetParser implements SchedulingConfigurer {

    private final Logger log = LoggerFactory.getLogger(getClass());

    static final String ASSET_PARSER_CLIENT = "c532e487ed41476e9c546dc2fd8ee56b";

    private final JsonRequestService requestService;
    private final AssetRepository assetRepository;
    private final TypeService typeService;
    private final LocationService locationService;
    private final TokenRepository tokenRepository;
    private final Environment env;
    private final AppraisalService appraisalService;
    private final NetWorthHistoryRepository assetHistoryRepository;

    public AssetParser(final JsonRequestService requestService, final AssetRepository assetRepository,
                       final TypeService typeService,
                       final LocationService locationService,
                       final TokenRepository tokenRepository, final Environment env,
                       final AppraisalService appraisalService,
                       final NetWorthHistoryRepository assetHistoryRepository) {
        this.requestService = requestService;
        this.assetRepository = assetRepository;
        this.typeService = typeService;
        this.locationService = locationService;
        this.tokenRepository = tokenRepository;
        this.env = env;
        this.appraisalService = appraisalService;
        this.assetHistoryRepository = assetHistoryRepository;
    }

    @Async
    public void refreshAssets() {
        log.info("Refreshing assets.");

        final Token token = tokenRepository.findByClientId(ASSET_PARSER_CLIENT).get(0);
        final String accessToken;
        try {
            accessToken = requestService.getAccessToken(token);
        } catch (UnirestException e) {
            log.error("Failed to get access token for refreshAssets.", e);
            return;
        }

        log.info("Collecting assets from esi.");
        final Collection<Asset> assets = collectAssets(accessToken);
        if (assets.isEmpty()) {
            log.info("No assets were returned. Aborting.");
            return;
        }

        log.info("Building location hierarchy.");
        final Map<Long, Long> locationHierarchy = buildLocationHierarchy(assets);

        log.info("Enhancing assets information.");
        final List<Asset> enhancedAssets = assets.stream().peek(asset -> {
            long locationId = asset.getLocationId();
            while (locationHierarchy.containsKey(locationId)) {
                locationId = locationHierarchy.get(locationId);
            }
            final String locationName = locationService.fetchStructureName(locationId);
            if (locationName.equals("N/A")) {
                log.info("Could not resolve location for asset {}.", asset);
            }
            asset.setLocationName(locationName);
        }).filter(asset -> !asset.getLocationName().equals("N/A"))
          .peek(asset -> {
            final String typeName = typeService.getNameByTypeId(asset.getTypeId());
            asset.setTypeName(typeName);
            final double volume = typeService.getVolume(asset.getTypeId());
            asset.setVolume(volume);
        }).collect(Collectors.toList());

        final Map<String, Double> prices = new HashMap<>();
        final List<String> typeNames = enhancedAssets.stream().map(Asset::getTypeName).distinct().collect(Collectors.toList());

        log.info("Appraising {} items.", typeNames.size());
        final int batchSize = 100;
        for (int i = 0; i < typeNames.size(); i+= batchSize) {

            try {
                final Appraisal appraisal = appraisalService.getAppraisalFromList(typeNames.subList(i, i + batchSize
                                                                                                       > typeNames.size() ? typeNames.size() - 1 : i + batchSize));
                for (final ItemWithQuantity item : appraisal.getItems()) {
                    prices.put(item.getTypeName(), item.getJitaBuyPerUnit());
                }
            } catch (AppraisalFailed e) {
                log.warn("AppraisalFailed, stopping the asset parsing.", e);
                return;
            }
        }
        log.info("Appraising complete.");

        final List<Asset> pricedAssets = enhancedAssets.stream().peek(asset -> asset.setPrice(prices.get(asset.getTypeName())))
                                          .collect(Collectors.toList());

        log.info("Writing assets.");
        assetRepository.deleteAll();
        assetRepository.save(pricedAssets);
        log.info("Asset parsing complete. {} entries have been added.", assets.size());
    }

    private Collection<Asset> collectAssets(final String accessToken) {
        final Collection<Asset> assets = new ArrayList<>();
        boolean nextPageAvailable = true;
        int pageCounter = 1;

        while (nextPageAvailable) {
            final Optional<JsonNode> jsonNode = requestService.getAssets(accessToken, pageCounter);
            if (jsonNode.isPresent()) {
                final JSONArray assetsArray = jsonNode.get().getArray();
                for (int i = 0; i < assetsArray.length(); i++) {
                    final JSONObject asset = assetsArray.getJSONObject(i);
                    final long typeId = asset.getLong("type_id");
                    final long itemId = asset.getLong("item_id");
                    final String locationFlag = asset.getString("location_flag");
                    final long locationId = asset.getLong("location_id");
                    final long quantity = asset.getLong("quantity");
                    assets.add(new Asset(itemId, typeId, quantity, locationId, locationFlag));
                }
                if (assetsArray.length() < 1000) {
                    nextPageAvailable = false;
                } else {
                    pageCounter++;
                }
            } else {
                nextPageAvailable = false;
            }
        }
        return assets;
    }

    private Map<Long, Long> buildLocationHierarchy(final Collection<Asset> assetsArray) {
        final Map<Long, Long> result = new HashMap<>();
        assetsArray.forEach(asset -> {
            if (asset.getItemId() != asset.getLocationId()) {
                result.put(asset.getItemId(), asset.getLocationId());
            }
        });
        log.debug("Completed buildLocationHierarchy with {} entries for {} assets.", result.size(), assetsArray.size());
        return result;
    }

    @Bean
    public ThreadPoolTaskScheduler taskExecutor() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.initialize();
        return scheduler;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        if (env.acceptsProfiles(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT)) {
            return;
        }
        taskRegistrar.setScheduler(taskExecutor());
        taskRegistrar.addTriggerTask(
            this::refreshAssets,
            triggerContext -> Date.from(requestService.getNextExecutionTime("corpAssets")));
    }

}
