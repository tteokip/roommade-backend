package com.roommade.domain.house.controller;

import com.roommade.domain.house.code.HouseSuccessCode;
import com.roommade.domain.house.dto.request.HouseRegisterRequest;
import com.roommade.domain.house.dto.response.HouseComparisonCurrentResponse;
import com.roommade.domain.house.service.HouseComparisonService;
import com.roommade.global.response.ApiResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/house-comparisons")
public class HouseComparisonController {

    private final HouseComparisonService houseComparisonService;

    /**
     * 인증이 아직 없어, 사용자 식별을 필수 헤더 {@code X-User-Id}로 임시 대체한다.
     * 검증되지 않은 값이므로 클라이언트가 임의로 다른 사용자 ID를 보낼 수 있다 — 실제 인증(JWT 등)이
     * 도입되면 이 헤더 방식을 인증 컨텍스트에서 사용자 ID를 얻는 방식으로 교체해야 한다.
     */
    @GetMapping("/current")
    public ApiResponse<HouseComparisonCurrentResponse> getCurrentComparison(
            @RequestHeader("X-User-Id") Long userId) {
        HouseComparisonCurrentResponse response = houseComparisonService.getCurrentComparison(userId);
        return ApiResponse.success(HouseSuccessCode.HOUSE_COMPARISON_CURRENT_FOUND, response);
    }

    /**
     * {@code houseType}의 A/B 슬롯 최초 등록만 담당한다. 이미 등록된 슬롯이면 409로 거부하며
     * 덮어쓰지 않는다 — 교체는 별도 PUT API에서 다룰 예정이다.
     */
    @PostMapping("/current/houses/{houseType}")
    public ResponseEntity<ApiResponse<HouseComparisonCurrentResponse>> registerHouse(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable String houseType,
            @Valid @RequestBody HouseRegisterRequest request) {
        HouseComparisonCurrentResponse response =
                houseComparisonService.registerHouse(userId, houseType, request);
        return ResponseEntity.status(HouseSuccessCode.HOUSE_REGISTERED.getStatus())
                .body(ApiResponse.success(HouseSuccessCode.HOUSE_REGISTERED, response));
    }
}
