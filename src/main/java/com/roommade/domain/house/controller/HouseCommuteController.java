package com.roommade.domain.house.controller;

import com.roommade.domain.house.code.HouseSuccessCode;
import com.roommade.domain.house.dto.response.HouseCommuteEstimateResponse;
import com.roommade.domain.house.service.HouseCommuteService;
import com.roommade.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/houses")
public class HouseCommuteController {

    private final HouseCommuteService houseCommuteService;

    @GetMapping("/commute-estimate")
    public ApiResponse<HouseCommuteEstimateResponse> estimate(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) String location) {
        HouseCommuteEstimateResponse response = houseCommuteService.estimate(userId, location);
        return ApiResponse.success(HouseSuccessCode.COMMUTE_ESTIMATED, response);
    }
}
