package com.thebuyback.eve.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.thebuyback.eve.domain.Asset;
import com.thebuyback.eve.domain.AssetOverview;
import com.thebuyback.eve.domain.ItemWithQuantity;
import com.thebuyback.eve.repository.AssetRepository;
import com.thebuyback.eve.web.dto.AssetsPerSystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class AssetService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final MongoTemplate mongoTemplate;
    private final AssetRepository assetRepository;
    private final List<String> HUBS = Arrays.asList("68FT-6 - Mothership Bellicose", "GE-8JV - BROADCAST4REPS");

    public AssetService(final MongoTemplate mongoTemplate, final AssetRepository assetRepository) {
        this.mongoTemplate = mongoTemplate;
        this.assetRepository = assetRepository;
    }

    public List<Asset> findAssets(Set<String> lines) {
        // limit to GE Fort and 68 KS
        List<Asset> assets = assetRepository.findAllByTypeNameInAndLocationNameIn(lines, HUBS);

        if (assets.isEmpty()) {
            // fallback to evepraisal parser if someone was too dumb to read the instructions
            try {
                final String link = AppraisalUtil.getLinkFromRaw(String.join(",", lines));
                final Set<String> items = AppraisalUtil.getItems(link).stream().map(ItemWithQuantity::getTypeName)
                                                         .collect(Collectors.toSet());
                assets = assetRepository.findAllByTypeNameInAndLocationNameIn(items, HUBS);
            } catch (UnirestException e) {
                log.error("Failed to load appraisal for assets. {}", lines, e);
            }
        }

        return assets;
    }

    public AssetOverview getAssetsForOverview(final String region, final String isHub) {
        final List<Asset> assets = getAssets(region, isHub);

        final double stuffValue = assets.stream()
                                        .filter(asset -> !asset.getTypeName().startsWith("Compressed"))
                                        .mapToDouble(asset -> asset.getQuantity() * asset.getPrice())
                                        .sum();

        final double stuffVolume = assets.stream()
                                         .filter(asset -> !asset.getTypeName().startsWith("Compressed"))
                                         .mapToDouble(asset -> asset.getQuantity() * asset.getVolume())
                                         .sum();

        final double oreValue = assets.stream()
                                        .filter(asset -> asset.getTypeName().startsWith("Compressed"))
                                        .mapToDouble(asset -> asset.getQuantity() * asset.getPrice())
                                        .sum();

        final double oreVolume = assets.stream()
                                         .filter(asset -> asset.getTypeName().startsWith("Compressed"))
                                         .mapToDouble(asset -> asset.getQuantity() * asset.getVolume())
                                         .sum();

        return new AssetOverview(oreVolume, oreValue, stuffVolume, stuffValue);
    }

    private List<Asset> getAssets(final String region, final String isHub) {
        final List<String> systems = new ArrayList<>();
        String locationName = null;
        if (region.equals("442-CS")) {
            if (isHub.equals("hub")) {
                locationName = "442-CS - Has Midslots";
            } else {
                systems.addAll(Arrays.asList("442-CS", "9ZFH-Z", "Z-N9IP", "PZMA-E", "TWJ-AW", "4-MPSJ", "N-7ECY"));
            }
        } else if (region.equals("68FT-6")) {
            if (isHub.equals("hub")) {
                locationName = "68FT-6 - Mothership Bellicose";
            } else {
                systems.addAll(Arrays.asList("68FT-6", "AFJ-NB", "IV-UNR", "YALR-F", "H-64KI", "9I-SRF", "9-IIBL", "5GQ-S9", "HOHF-B", "Y-6B0E", "IRE-98"));
            }
        } else if (region.equals("TM-0P2")) {
            if (isHub.equals("hub")) {
                locationName = "1L"; // todo: get real ID for tm- fort
            } else {
                systems.addAll(Arrays.asList("TM-0P2", "E3-SDZ", "N-CREL", "4OIV-X", "Y-JKJ8"));
            }
        } else if (region.equals("GE-8JV")) {
            if (isHub.equals("hub")) {
                locationName = "GE-8JV - BROADCAST4REPS";
            } else {
                systems.addAll(Arrays.asList("GE-8JV", "AOK-WQ", "B-XJX4", "7LHB-Z", "E1-Y4H", "MUXX-4", "AX-DOT", "YHN-3K", "V-3YG7", "3-OKDA", "4M-HGL", "MY-W1V", "8B-2YA", "SNFV-I", "HP-64T"));
            }
        }

        final List<Asset> assets;
        if (locationName != null) {
            assets = assetRepository.findAllByLocationName(locationName);
        } else {
            assets = assetRepository.findAll().stream()
                           .filter(asset -> startsWithAnyOf(asset.getLocationName(), systems))
                           .collect(Collectors.toList());
        }
        return assets.stream().filter(asset -> asset.getTypeName() != null && !asset.getTypeName().contains("Blueprint")).collect(Collectors.toList());
    }

    boolean startsWithAnyOf(final String locationName, final Iterable<String> systems) {
        for (String system : systems) {
            if (locationName.startsWith(system)) {
                return true;
            }
        }
        return false;
    }

    public Set<AssetsPerSystem> getAssetsForOverviewDetails(final String region, final String isHub) {
        final List<Asset> assets = getAssets(region, isHub);
        final Set<AssetsPerSystem> result = new HashSet<>();

        for (final Asset asset : assets) {
            if (asset.getLocationName() == null || asset.getLocationName().equals("N/A")) {
                continue;
            }
            final String systemName = asset.getLocationName().split(" ")[0];
            final Optional<AssetsPerSystem> first = result.stream().filter(a -> a.getSystemName().equals(systemName)).findFirst();
            final double stuffWorth = asset.getQuantity() * asset.getPrice();
            final double stuffVolume = asset.getQuantity() * asset.getVolume();
            AssetsPerSystem assetsPerSystem;
            if (first.isPresent()) {
                assetsPerSystem = first.get();
                assetsPerSystem.addStuffWorth(stuffWorth);
                assetsPerSystem.addStuffVolume(stuffVolume);
            } else {
                assetsPerSystem = new AssetsPerSystem(systemName, stuffWorth, stuffVolume);
            }
            result.add(assetsPerSystem);
        }

        return result;
    }
}
