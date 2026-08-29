package com.roommade.domain.financialproduct.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roommade.domain.financialproduct.dto.external.FssProductApiResponse;
import com.roommade.global.exception.BusinessException;
import com.roommade.global.exception.CommonErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FssProductApiClient {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String baseUrl;
    private final String auth;
    private final String depositPath;
    private final String savingPath;
    private final String topFinancialGroupNumber;
    private final String financialCompanyCode;

    public FssProductApiClient(@Value("${fss.api.base-url:https://finlife.fss.or.kr}") String baseUrl,
            @Value("${fss.api.auth:}") String auth,
            @Value("${fss.api.deposit-path:/depositProductsSearch.json}") String depositPath,
            @Value("${fss.api.saving-path:/savingProductsSearch.json}") String savingPath,
            @Value("${fss.api.top-fin-grp-no:020000}") String topFinancialGroupNumber,
            @Value("${fss.api.financial-company-code:0010927}") String financialCompanyCode) {
        this.baseUrl = baseUrl; this.auth = auth; this.depositPath = depositPath;
        this.savingPath = savingPath; this.topFinancialGroupNumber = topFinancialGroupNumber;
        this.financialCompanyCode = financialCompanyCode;
    }

    public FssProductApiResponse getDepositProducts() { return request(depositPath); }
    public FssProductApiResponse getSavingProducts() { return request(savingPath); }
    public boolean isConfigured() { return auth != null && !auth.isBlank(); }
    public String getFinancialCompanyCode() { return financialCompanyCode; }

    private FssProductApiResponse request(String path) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl).path(path)
                    .queryParam("auth", auth).queryParam("topFinGrpNo", topFinancialGroupNumber)
                    .queryParam("pageNo", 1).encode().toUriString();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));
            headers.set(HttpHeaders.USER_AGENT, "ROOMMADE/1.0");
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
            String body = response.getBody();
            if (body == null || body.isBlank()) throw new IllegalStateException("빈 응답");
            return objectMapper.readValue(body, FssProductApiResponse.class);
        } catch (Exception exception) {
            log.error("금융감독원 금융상품 API 호출에 실패했습니다. path={}", path, exception);
            throw new BusinessException(CommonErrorCode.SERVICE_UNAVAILABLE);
        }
    }
}
