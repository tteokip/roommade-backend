package com.roommade.domain.house.controller;

import com.roommade.domain.house.code.HouseSuccessCode;
import com.roommade.domain.house.dto.request.HouseConfirmRequest;
import com.roommade.domain.house.dto.response.HouseConfirmationResponse;
import com.roommade.domain.house.service.HouseConfirmationService;
import com.roommade.global.response.ApiResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/house-confirmations")
public class HouseConfirmationController {

    private final HouseConfirmationService houseConfirmationService;

    @PostMapping
    public ResponseEntity<ApiResponse<HouseConfirmationResponse>> confirmHouse(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody HouseConfirmRequest request) {
        HouseConfirmationResponse response =
                houseConfirmationService.confirmHouse(userId, request);
        return ResponseEntity.status(HouseSuccessCode.HOUSE_CONFIRMED.getStatus())
                .body(ApiResponse.success(HouseSuccessCode.HOUSE_CONFIRMED, response));
    }
}
