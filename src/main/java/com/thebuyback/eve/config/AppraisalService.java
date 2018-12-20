package com.thebuyback.eve.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.body.RequestBodyEntity;
import com.thebuyback.eve.domain.Appraisal;
import com.thebuyback.eve.domain.AppraisalFailed;
import com.thebuyback.eve.domain.ItemBuybackRate;
import com.thebuyback.eve.domain.ItemWithQuantity;
import com.thebuyback.eve.domain.TypeBuybackRate;
import com.thebuyback.eve.service.ItemBuybackRateService;
import com.thebuyback.eve.service.TypeService;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AppraisalService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final TypeService typeService;
    private final ItemBuybackRateService buybackRateService;
    private final LimitedQueue<Appraisal> cache = new LimitedQueue<>(100);

    public AppraisalService(final TypeService typeService,
                            final ItemBuybackRateService buybackRateService) {
        this.typeService = typeService;
        this.buybackRateService = buybackRateService;
    }

    public Appraisal getAppraisalFromNewLineSeparatedRaw(final String newLineSeparatedRaw) throws AppraisalFailed {
        final Appraisal appraisal = getFromRaw(newLineSeparatedRaw);
        cache.add(appraisal);
        return appraisal;
    }

    public Appraisal getAppraisalFromList(final Collection<String> list) throws AppraisalFailed {
        return getAppraisalFromNewLineSeparatedRaw(list.stream().collect(Collectors.joining("\n")));
    }

    public Appraisal getAppraisalFromTypeNameMap(final Map<String, Integer> map) throws AppraisalFailed {
        return getAppraisalFromNewLineSeparatedRaw(getRawFromTypeNames(map));
    }

    public Appraisal getAppraisalFromTypeIdMap(final Map<Integer, Integer> map) throws AppraisalFailed {
        return getAppraisalFromNewLineSeparatedRaw(getRawFromTypeIds(map));
    }

    public Appraisal getAppraisalFromId(final String appraisalId) throws AppraisalFailed {
        final Optional<Appraisal> first = cache.stream()
                                               .filter(appraisal -> linkMatchesId(appraisal.getLink(), appraisalId))
                                               .findFirst();
        if (first.isPresent()) {
            return first.get();
        } else {
            final Appraisal appraisal = getFromAppraisalId(appraisalId);
            cache.add(appraisal);
            return appraisal;
        }
    }

    private boolean linkMatchesId(final String link, final String appraisalId) {
        return link.endsWith(appraisalId);
    }

    private String getRawFromTypeIds(final Map<Integer, Integer> items) {
        return items.entrySet().stream().map(entry -> {
            String typeName = typeService.getNameByTypeId(entry.getKey());
            return typeName + " x" + entry.getValue();
        }).collect(Collectors.joining("\n"));
    }

    private String getRawFromTypeNames(final Map<String, Integer> items) {
        return items.entrySet().stream().map(entry -> entry.getKey() + " x" + entry.getValue())
                    .collect(Collectors.joining("\n"));
    }

    public Appraisal getFromAppraisalLink(final String appraisalLink) throws AppraisalFailed {
        final GetRequest request = Unirest.get(appraisalLink + ".json");
        return mapToAppraisal(getAppraisal(request), null);
    }

    private Appraisal getFromAppraisalId(final String appraisalId) throws AppraisalFailed {
        final GetRequest request = Unirest.get("http://evepraisal.com/a/" + appraisalId + ".json");
        return mapToAppraisal(getAppraisal(request), null);
    }

    private Appraisal getFromRaw(String raw) throws AppraisalFailed {
        log.debug("Getting appraisal for '{}'", raw.replace("\n", ";"));
        final RequestBodyEntity request = Unirest.post("http://evepraisal.com/appraisal.json?market=jita").body(raw);
        return mapToAppraisal(getAppraisal(request.getHttpRequest()), raw);
    }

    private JSONObject getAppraisal(final HttpRequest request) throws AppraisalFailed {
        HttpResponse<JsonNode> jsonHttpResponse;
        try {
            jsonHttpResponse = request.asJson();
        } catch (UnirestException e) {
            log.warn("Unirest failed.", e);
            throw new AppraisalFailed(request.getUrl(), request.getBody());
        }

        if (jsonHttpResponse.getStatus() != 200) {
            log.warn("Appraisal response was {} for url {}.", jsonHttpResponse.getStatusText(), request.getUrl());
            throw new AppraisalFailed(request.getUrl(), request.getBody(), jsonHttpResponse.getStatusText());
        }

        return jsonHttpResponse.getBody().getObject();
    }

    private Appraisal mapToAppraisal(final JSONObject rootObject, final String raw) {
        final JSONObject appraisalNode;
        if (rootObject.has("appraisal")) {
            appraisalNode = rootObject.getJSONObject("appraisal");
        } else {
            appraisalNode = rootObject;
        }

        final Appraisal appraisal = new Appraisal();
        if (appraisalNode.has("totals")) {
            final JSONObject totals = appraisalNode.getJSONObject("totals");
            appraisal.setJitaSell(totals.getDouble("sell"));
            appraisal.setJitaBuy(totals.getDouble("buy"));
        }

        final List<ItemWithQuantity> items = parseItems(appraisalNode.getJSONArray("items"));
        appraisal.setItems(items);
        appraisal.setLink("http://evepraisal.com/a/" + appraisalNode.getString("id"));

        appraisal.setBuybackPrice(getBuybackPrice(items));
        appraisal.setRaw(raw);
        return appraisal;
    }

    private double getBuybackPrice(final Iterable<ItemWithQuantity> items) {
        double sum = 0;
        for (ItemWithQuantity item : items) {
            sum += item.getJitaBuyPerUnit() * item.getRate() * item.getQuantity();
        }
        return sum;
    }

    private List<ItemWithQuantity> parseItems(final JSONArray items) {
        if (items.length() == 0) {
            return Collections.emptyList();
        }

        final List<ItemWithQuantity> result = new ArrayList<>();

        for (int i = 0; i < items.length(); i++) {
            final JSONObject jsonItem = items.getJSONObject(i);
            final ItemWithQuantity item = parseItem(jsonItem);
            result.add(item);
        }

        setRates(result);

        return result;
    }

    private void setRates(final Collection<ItemWithQuantity> items) {
        final Set<Long> typeIds = items.stream().map(ItemWithQuantity::getTypeID).collect(Collectors.toSet());
        final List<TypeBuybackRate> rates = buybackRateService.getRates(typeIds);

        // set defined rates
        for (TypeBuybackRate rate : rates) {
            for (ItemWithQuantity item : items) {
                if (rate.getTypeId() == item.getTypeID()) {
                    item.setRate(rate.getRate());
                }
            }
        }

        // set default 90% for undefined rates
        for (ItemWithQuantity item : items) {
            if (item.getRate() == null) {
                item.setRate(0.9);
            }
        }
    }

    private ItemWithQuantity parseItem(final JSONObject jsonItem) {
        final ItemWithQuantity item = new ItemWithQuantity();
        item.setJitaBuyPerUnit(getJitaBuyForItem(jsonItem));
        item.setQuantity(jsonItem.getInt("quantity"));
        item.setTypeID(jsonItem.getLong("typeID"));
        item.setTypeName(jsonItem.getString("typeName"));
        return item;
    }

    private double getJitaBuyForItem(final JSONObject jsonItem) {
        if (jsonItem.has("prices") && jsonItem.getJSONObject("prices").has("buy")) {
            final JSONObject buy = jsonItem.getJSONObject("prices").getJSONObject("buy");
            return buy.getDouble("max");
        }
        return 0;
    }

    private static class LimitedQueue<E> extends LinkedList<E> {

        private static final long serialVersionUID = -7922444728484144237L;
        private int limit;

        public LimitedQueue(int limit) {
            this.limit = limit;
        }

        @Override
        public boolean add(E e) {
            boolean added = super.add(e);
            while (added && size() > limit) {
                remove();
            }
            return added;
        }
    }

}
