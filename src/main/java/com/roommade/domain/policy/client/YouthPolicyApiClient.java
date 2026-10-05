package com.roommade.domain.policy.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roommade.domain.policy.dto.external.YouthPolicyApiResponse;
import com.roommade.global.exception.BusinessException;
import com.roommade.global.exception.CommonErrorCode;
import java.net.URI;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
public class YouthPolicyApiClient {
    private static final int SUCCESS_RESULT_CODE = 200;
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long RETRY_DELAY_MILLIS = 1_000L;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private final String baseUrl;
    private final String policyPath;
    private final String apiKey;
    private final Integer pageSize;

    public YouthPolicyApiClient(
            @Value("${youth-policy.api.base-url:https://www.youthcenter.go.kr}") String baseUrl,
            @Value("${youth-policy.api.policy-path:/go/ythip/getPlcy}") String policyPath,
            @Value("${youth-policy.api.key:}") String apiKey,
            @Value("${youth-policy.api.page-size:10}") Integer pageSize) {
        this.baseUrl = baseUrl;
        this.policyPath = policyPath;
        this.apiKey = apiKey;
        this.pageSize = pageSize;
    }

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }

    public YouthPolicyApiResponse getYouthPolicies(int pageNum, String policyKeyword) {
        for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
            try {
                return request(pageNum, policyKeyword);
            } catch (RuntimeException exception) {
                if (attempt == MAX_RETRY_ATTEMPTS) {
                    log.error("온통청년 정책 API 호출에 실패했습니다. page={}, keyword={}", pageNum, policyKeyword, exception);
                    throw new BusinessException(CommonErrorCode.SERVICE_UNAVAILABLE);
                }
                log.warn("온통청년 정책 API 호출에 실패해 재시도합니다. page={}, keyword={}, attempt={}",
                        pageNum, policyKeyword, attempt, exception);
                waitBeforeRetry(attempt);
            }
        }
        throw new BusinessException(CommonErrorCode.SERVICE_UNAVAILABLE);
    }

    private YouthPolicyApiResponse request(int pageNum, String policyKeyword) {
        try {
            URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                    .path(policyPath)
                    .queryParam("apiKeyNm", apiKey)
                    .queryParam("pageNum", pageNum)
                    .queryParam("pageSize", pageSize)
                    .queryParam("rtnType", "json")
                    .queryParam("plcyKywdNm", policyKeyword)
                    .build()
                    .encode()
                    .toUri();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.set(HttpHeaders.USER_AGENT, "ROOMMADE/1.0");
            ResponseEntity<String> response = restTemplate.exchange(
                    uri, HttpMethod.GET, new HttpEntity<>(headers), String.class);
            String body = response.getBody();
            if (body == null || body.isBlank()) {
                throw new IllegalStateException("빈 응답");
            }
            YouthPolicyApiResponse parsed = objectMapper.readValue(body, YouthPolicyApiResponse.class);
            if (!Integer.valueOf(SUCCESS_RESULT_CODE).equals(parsed.getResultCode())) {
                throw new IllegalStateException("온통청년 API 오류 응답");
            }
            return parsed;
        } catch (Exception exception) {
            throw new IllegalStateException("온통청년 정책 API 호출 실패", exception);
        }
    }

    private void waitBeforeRetry(int attempt) {
        try {
            Thread.sleep(RETRY_DELAY_MILLIS * attempt);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new BusinessException(CommonErrorCode.SERVICE_UNAVAILABLE);
        }
    }
}
