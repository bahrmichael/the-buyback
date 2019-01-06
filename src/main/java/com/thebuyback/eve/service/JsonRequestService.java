package com.thebuyback.eve.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.singletonList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.BaseRequest;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.body.MultipartBody;
import com.mashape.unirest.request.body.RequestBodyEntity;
import com.thebuyback.eve.domain.Token;

import static com.thebuyback.eve.web.rest.ContractsResource.THE_BUYBACK;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class JsonRequestService {

    private static final String USER_AGENT = "EvE: Rihan Shazih";
    private static final String WRONG_STATUS_CODE = "{} returned status code {}.";
    private static final String UNIREST_EXCEPTION = "Failed to get data from url={}";
    private static final String BODY_TEMPLATE = "{\"recipients\": [{\"recipient_type\": \"character\",\"recipient_id\": %d}, {\"recipient_type\": \"corporation\",\"recipient_id\": 98503372}],\"subject\": \"The Buyback - %s\",\"body\": \"%s\"}";
    private static final long CORPORATION = 98503372L;
    private static final long MAIL_CHAR = 93475128L;
    private static final String ESI_BASE_URL = "https://esi.evetech.net";
    private static final String BACK_OFF = "backOff";
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Map<String, String> defaultHeaders;
    private final Map<String, Instant> esiCacheExpiries = new HashMap<>();

    private static final DateTimeFormatter EXPIRY_FORMATTER = new DateTimeFormatterBuilder()
        .appendPattern("EEE, dd MMM yyyy HH:mm:ss zzz")
        .toFormatter()
        .withZone(ZoneOffset.UTC);

    public JsonRequestService() {
        defaultHeaders = new HashMap<>();
        defaultHeaders.put("X-User-Agent", USER_AGENT);
        defaultHeaders.put("Accept-Encoding", "gzip");

        // Only one time
        Unirest.setObjectMapper(new ObjectMapper() {
            private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper
                = new com.fasterxml.jackson.databind.ObjectMapper();

            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return jacksonObjectMapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public String writeValue(Object value) {
                try {
                    return jacksonObjectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public Instant getNextExecutionTime(final String useCase) {
        if (esiCacheExpiries.containsKey(BACK_OFF) && esiCacheExpiries.get(BACK_OFF).isAfter(Instant.now())) {
            return esiCacheExpiries.get(BACK_OFF);
        }
        return esiCacheExpiries.containsKey(useCase) ? esiCacheExpiries.get(useCase) : Instant.now().plusSeconds(60);
    }

    public String getAccessToken(final Token token) throws UnirestException {
        HttpResponse<JsonNode> response = Unirest.post("https://login.eveonline.com/oauth/token")
                                                 .headers(defaultHeaders)
                                                 .field("grant_type","refresh_token")
                                                 .field("refresh_token", token.getRefreshToken())
                                                 .basicAuth(token.getClientId(), token.getClientSecret())
                                                 .asJson();
        final JSONObject object = response.getBody().getObject();
        return object.getString("access_token");
    }

    public Optional<JsonNode> getAccessToken(final String clientId, final String clientSecret, final String code) {
        String url = "https://login.eveonline.com/oauth/token";
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        Map<String, Object> fields = new HashMap<>();
        fields.put("grant_type", "authorization_code");
        fields.put("code", code);

        MultipartBody postRequest = post(url, clientId, clientSecret, headers, fields);

        return executeRequest(postRequest);
    }

    public Optional<JsonNode> getUserDetails(final String accessToken) {
        String url = "https://login.eveonline.com/oauth/verify";
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + accessToken);

        GetRequest getRequest = get(url, headers);

        return executeRequest(getRequest);
    }

    private Optional<JsonNode> justGet(final String url, final String cachingUseCase) {
        return executeRequest(get(url, null), cachingUseCase);
    }

    Optional<JsonNode> executeRequest(final BaseRequest request) {
        return executeRequest(request, null);
    }

    Optional<JsonNode> executeRequest(final BaseRequest request, final String cachingUserCase) {
        try {
            if (esiCacheExpiries.containsKey(BACK_OFF) && esiCacheExpiries.get(BACK_OFF).isAfter(Instant.now())) {
                log.info("Waiting until backOff ends.");
                return Optional.empty();
            }
            HttpResponse<JsonNode> response = request.asJson();
            // warn if deprecated
            if (response.getHeaders().containsKey("warning")) {
                final String warning = response.getHeaders().getFirst("warning");
                log.warn("Deprecation: {}, {}", warning, request.getHttpRequest().getUrl());
            }
            if (response.getStatus() != 200) {
                if (Arrays.asList(420, 502, 503).contains(response.getStatus())) {
                    esiCacheExpiries.put(BACK_OFF, Instant.now().plus(5, ChronoUnit.MINUTES));
                }
                log.warn(WRONG_STATUS_CODE, request.getHttpRequest().getUrl(), response.getStatus());
                return Optional.empty();
            }
            if (null != cachingUserCase) {
                final String expires = response.getHeaders().getFirst("Expires");
                if (null != expires) {
                    esiCacheExpiries.put(cachingUserCase, parseInstant(expires).plus(1, ChronoUnit.MINUTES));
                }
            }
            return Optional.of(response.getBody());
        } catch (UnirestException e) {
            log.error(UNIREST_EXCEPTION, request.getHttpRequest().getUrl(), e);
            return Optional.empty();
        }
    }

    private Instant parseInstant(final CharSequence dateString) {
        return EXPIRY_FORMATTER.parse(dateString, Instant::from);
    }

    GetRequest get(String url, Map<String, String> headers) {
        return Unirest.get(url).headers(defaultHeaders).headers(headers);
    }

    MultipartBody post(final String url, final String username, final String password, final Map<String, String> headers, final Map<String, Object> fields) {
        return Unirest.post(url).basicAuth(username, password).headers(defaultHeaders).headers(headers).fields(fields);
    }

    RequestBodyEntity post(final String url, final String body, final Map<String, String> headers) {
        final JsonNode node = new JsonNode(body);
        return Unirest.post(url).headers(headers).body(node);
    }

    Optional<JsonNode> getCorpContracts(final String accessToken, final int page) {
        return justGet(String.format("%s/v1/corporations/%d/contracts/?token=%s&page=%d", ESI_BASE_URL, CORPORATION, accessToken, page), "corpContracts");
    }

    Optional<JsonNode> getCorpContractItems(final long contractId, final String accessToken) {
        return justGet(String.format("%s/v1/corporations/%d/contracts/%d/items/?token=%s", ESI_BASE_URL, CORPORATION, contractId, accessToken), "corpContractItems");
    }

    Optional<JsonNode> getCorporationInfo(final long corporationId) {
        return justGet(String.format("%s/v4/corporations/%d/", ESI_BASE_URL, corporationId), null);
    }

    Optional<JsonNode> getCharacterName(final long characterId) {
        String url = "https://esi.evetech.net/v2/universe/names/";
        final RequestBodyEntity request = Unirest.post(url).headers(defaultHeaders).body(singletonList(characterId));
        return executeRequest(request, "characterName");
    }

    Optional<JsonNode> getTypeInfo(final long typeId) {
        return justGet(String.format("%s/v2/universe/types/%d", ESI_BASE_URL, typeId), null);
    }

    Optional<JsonNode> searchType(final String typeName) {
        final String encodedTYpeName;
        try {
            encodedTYpeName = URLEncoder.encode(typeName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return justGet(String.format("%s/v2/search/?categories=inventory_type&search=%s&strict=true", ESI_BASE_URL,
                                     encodedTYpeName), null);
    }

    Optional<JsonNode> getGroupInfo(final long groupId) {
        return justGet(String.format("%s/v1/universe/groups/%d", ESI_BASE_URL, groupId), null);
    }

    Optional<JsonNode> getCategoryInfo(final long categoryId) {
        return justGet(String.format("%s/v1/universe/categories/%d", ESI_BASE_URL, categoryId), null);
    }

    Optional<JsonNode> getAssets(final String accessToken, final int page) {
        return justGet(String.format("%s/v3/corporations/%d/assets/?token=%s&page=%d", ESI_BASE_URL, THE_BUYBACK, accessToken, page), "corpAssets");
    }

    Optional<JsonNode> getStructureInfo(final long structureId, final String accessToken) {
        String url = String.format("%s/v2/universe/structures/%d/?token=%s", ESI_BASE_URL, structureId, accessToken);
        return justGet(url, null);
    }

    Optional<JsonNode> getStationInfo(final long stationId) {
        String url = String.format("%s/v2/universe/stations/%d/", ESI_BASE_URL, stationId);
        return justGet(url, null);
    }

    public Optional<String> sendMail(final long recipientId, final String title, final String mail, final String accessToken) {
        final String body = String.format(BODY_TEMPLATE, recipientId, title, mail);
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + accessToken);
        headers.put("Content-Type", "application/json");
        RequestBodyEntity request = post(String.format("%s/v1/characters/%d/mail/", ESI_BASE_URL, MAIL_CHAR), body, headers);

        try {
            HttpResponse<String> response = request.asString();
            // warn if deprecated
            if (response.getHeaders().containsKey("warning")) {
                final String warning = response.getHeaders().getFirst("warning");
                log.warn("Deprecation: {}, {}", warning, request.getHttpRequest().getUrl());
            }
            if (response.getStatus() != 201) {
                log.warn(WRONG_STATUS_CODE, request.getHttpRequest().getUrl(), response.getStatus());
                return Optional.empty();
            }
            return Optional.of(response.getBody());
        } catch (UnirestException e) {
            log.error(UNIREST_EXCEPTION, request.getHttpRequest().getUrl(), e);
            return Optional.empty();
        }
    }

    public Optional<JsonNode> getMasterWalletBalance(final String accessToken) {
        String url = String.format("%s/v1/corporations/%d/wallets/?token=%s", ESI_BASE_URL, THE_BUYBACK, accessToken);
        return justGet(url, accessToken);
    }

    public Optional<JsonNode> getCorpMarketOrders(final String accessToken) {
        String url = String.format("%s/v2/corporations/%d/orders/?token=%s", ESI_BASE_URL, THE_BUYBACK, accessToken);
        return justGet(url, accessToken);
    }
}
