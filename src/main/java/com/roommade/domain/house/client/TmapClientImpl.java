package com.roommade.domain.house.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roommade.domain.house.dto.external.TmapGeocodeResponse;
import com.roommade.domain.house.dto.external.TmapTransitRouteResponse;
import com.roommade.global.exception.BusinessException;
import com.roommade.global.exception.CommonErrorCode;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
public class TmapClientImpl implements TmapClient {

    private static final String GEOCODE_URL = "https://apis.openapi.sk.com/tmap/geo/fullAddrGeo";
    private static final String TRANSIT_URL = "https://apis.openapi.sk.com/transit/routes";
    private static final int GEOCODE_RESULT_COUNT = 20;
    private static final long CACHE_TTL_MILLIS = 24 * 60 * 60 * 1000L;
    private static final int GEOCODE_CACHE_MAX_SIZE = 500;
    private static final int ROUTE_CACHE_MAX_SIZE = 2_000;
    private static final int CONNECT_TIMEOUT_MILLIS = 3_000;
    private static final int READ_TIMEOUT_MILLIS = 10_000;
    private static final String TRANSIT_MODE = "TRANSIT";

    private final RestTemplate restTemplate = createRestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private final String appKey;

    private final TtlCache<String, List<TmapGeocodeCandidate>> geocodeCache =
            new TtlCache<>(CACHE_TTL_MILLIS, GEOCODE_CACHE_MAX_SIZE);
    private final TtlCache<RouteCacheKey, Optional<Integer>> routeCache =
            new TtlCache<>(CACHE_TTL_MILLIS, ROUTE_CACHE_MAX_SIZE);

    public TmapClientImpl(@Value("${tmap.api.key:}") String appKey) {
        this.appKey = appKey;
    }

    @Override
    public List<TmapGeocodeCandidate> geocode(String query) {
        String normalized = normalize(query);
        // 같은 지역을 반복 조회할 때 외부 API를 다시 호출하지 않도록 지오코딩 결과를 캐시한다.
        return geocodeCache.getOrCompute(normalized, () -> requestGeocode(normalized));
    }

    @Override
    public Optional<Integer> getTransitMinutes(double startLat, double startLon, double endLat, double endLon) {
        RouteCacheKey key = new RouteCacheKey(startLat, startLon, endLat, endLon, TRANSIT_MODE);
        // 경로 결과는 출발·도착 좌표와 교통수단이 모두 같을 때만 재사용한다.
        return routeCache.getOrCompute(key,
                () -> requestTransitMinutes(startLat, startLon, endLat, endLon));
    }

    private String normalize(String query) {
        return query.trim().replaceAll("\\s+", " ");
    }

    private List<TmapGeocodeCandidate> requestGeocode(String query) {
        try {
            URI uri = UriComponentsBuilder.fromHttpUrl(GEOCODE_URL)
                    .queryParam("version", 1)
                    .queryParam("addressFlag", "F00")
                    .queryParam("coordType", "WGS84GEO")
                    .queryParam("fullAddr", query)
                    .queryParam("page", 1)
                    .queryParam("count", GEOCODE_RESULT_COUNT)
                    .build()
                    .encode()
                    .toUri();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.set("appKey", appKey);

            ResponseEntity<String> response =
                    restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(headers), String.class);
            TmapGeocodeResponse parsed = objectMapper.readValue(response.getBody(), TmapGeocodeResponse.class);
            if (parsed.getCoordinateInfo() == null
                    || parsed.getCoordinateInfo().getCoordinate() == null) {
                return List.of();
            }
            return parsed.getCoordinateInfo().getCoordinate().stream()
                    .map(this::toCandidate)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception exception) {
            log.error("TMAP 지오코딩 호출에 실패했습니다.", exception);
            throw new BusinessException(CommonErrorCode.SERVICE_UNAVAILABLE);
        }
    }

    private TmapGeocodeCandidate toCandidate(TmapGeocodeResponse.Coordinate coordinate) {
        Double lat = parseCoordinate(coordinate.getNewLat(), coordinate.getLat());
        Double lon = parseCoordinate(coordinate.getNewLon(), coordinate.getLon());
        if (lat == null || lon == null) {
            return null;
        }
        return new TmapGeocodeCandidate(
                lat,
                lon,
                coordinate.getCityDo(),
                coordinate.getGuGun(),
                coordinate.getLegalDong(),
                coordinate.getAdminDong());
    }

    private Double parseCoordinate(String preferred, String fallback) {
        if (StringUtils.hasText(preferred)) {
            return Double.parseDouble(preferred);
        }
        if (StringUtils.hasText(fallback)) {
            return Double.parseDouble(fallback);
        }
        return null;
    }

    private static RestTemplate createRestTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(CONNECT_TIMEOUT_MILLIS);
        requestFactory.setReadTimeout(READ_TIMEOUT_MILLIS);
        return new RestTemplate(requestFactory);
    }

    private Optional<Integer> requestTransitMinutes(
            double startLat, double startLon, double endLat, double endLon) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.set("appKey", appKey);
            Map<String, Object> body = Map.of(
                    "startX", String.valueOf(startLon),
                    "startY", String.valueOf(startLat),
                    "endX", String.valueOf(endLon),
                    "endY", String.valueOf(endLat),
                    "count", 1,
                    "lang", 0,
                    "format", "json");

            ResponseEntity<String> response = restTemplate.exchange(
                    TRANSIT_URL, HttpMethod.POST, new HttpEntity<>(body, headers), String.class);
            TmapTransitRouteResponse parsed =
                    objectMapper.readValue(response.getBody(), TmapTransitRouteResponse.class);
            if (parsed.getMetaData() == null
                    || parsed.getMetaData().getPlan() == null
                    || parsed.getMetaData().getPlan().getItineraries() == null
                    || parsed.getMetaData().getPlan().getItineraries().isEmpty()) {
                return Optional.empty();
            }
            int totalTimeSeconds = parsed.getMetaData().getPlan().getItineraries().get(0).getTotalTime();
            // TMAP은 초 단위로 반환하므로 사용자에게 보여줄 분 단위로 반올림한다.
            return Optional.of((int) Math.round(totalTimeSeconds / 60.0));
        } catch (Exception exception) {
            log.error("TMAP 대중교통 경로 호출에 실패했습니다.", exception);
            throw new BusinessException(CommonErrorCode.SERVICE_UNAVAILABLE);
        }
    }

    private record RouteCacheKey(double startLat, double startLon, double endLat, double endLon, String mode) {
    }
}
