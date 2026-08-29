package com.roommade.domain.house.controller;

import com.roommade.domain.house.code.HouseSuccessCode;
import com.roommade.domain.house.dto.response.HouseAnalysisResponse;
import com.roommade.domain.house.service.HouseAnalysisService;
import com.roommade.global.response.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/house-analyses")
public class HouseAnalysisController {

    private final HouseAnalysisService houseAnalysisService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<HouseAnalysisResponse> analyze(
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        HouseAnalysisResponse response = houseAnalysisService.analyze(images);
        return ApiResponse.success(HouseSuccessCode.HOUSE_ANALYZED, response);
    }
}
