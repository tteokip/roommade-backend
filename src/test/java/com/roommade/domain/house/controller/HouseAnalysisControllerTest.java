package com.roommade.domain.house.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.roommade.domain.house.code.HouseErrorCode;
import com.roommade.domain.house.dto.response.HouseAnalysisResponse;
import com.roommade.domain.house.dto.response.HouseAnalysisResultResponse;
import com.roommade.domain.house.dto.response.HouseAnalysisStatus;
import com.roommade.domain.house.service.HouseAnalysisService;
import com.roommade.global.exception.BusinessException;
import com.roommade.global.exception.GlobalExceptionHandler;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class HouseAnalysisControllerTest {

    @Mock
    private HouseAnalysisService houseAnalysisService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        HouseAnalysisController controller = new HouseAnalysisController(houseAnalysisService);
        mockMvc = standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("이미지 2장을 multipart로 보내면 Service에 그대로 전달되고 200으로 응답한다")
    void analyzesTwoImagesSuccessfully() throws Exception {
        MockMultipartFile image1 = new MockMultipartFile("images", "a.jpg", "image/jpeg", new byte[]{1, 2, 3});
        MockMultipartFile image2 = new MockMultipartFile("images", "b.png", "image/png", new byte[]{4, 5, 6});
        HouseAnalysisResultResponse house = new HouseAnalysisResultResponse(
                "서울시 광진구", 10_000_000L, 500_000L, 50_000L, null, 7, "3층", "원룸", "풀옵션");
        when(houseAnalysisService.analyze(anyList()))
                .thenReturn(new HouseAnalysisResponse(HouseAnalysisStatus.COMPLETED, house));

        mockMvc.perform(multipart("/api/house-analyses").file(image1).file(image2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value("HOUSE_005"))
                .andExpect(jsonPath("$.data.analysisStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.data.house.location").value("서울시 광진구"));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<MultipartFile>> captor = ArgumentCaptor.forClass(List.class);
        verify(houseAnalysisService).analyze(captor.capture());
        assertThat(captor.getValue()).hasSize(2);
    }

    @Test
    @DisplayName("Service가 이미지 개수 오류를 던지면 400을 반환한다")
    void returnsBadRequestWhenServiceRejectsImageCount() throws Exception {
        MockMultipartFile image = new MockMultipartFile("images", "a.jpg", "image/jpeg", new byte[]{1});
        when(houseAnalysisService.analyze(anyList()))
                .thenThrow(new BusinessException(HouseErrorCode.INVALID_IMAGE_COUNT));

        mockMvc.perform(multipart("/api/house-analyses").file(image))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("HOUSE_006"));
    }

    @Test
    @DisplayName("Service가 분석 실패 예외를 던지면 502를 반환한다")
    void returnsBadGatewayWhenAnalysisFails() throws Exception {
        MockMultipartFile image = new MockMultipartFile("images", "a.jpg", "image/jpeg", new byte[]{1});
        when(houseAnalysisService.analyze(anyList()))
                .thenThrow(new BusinessException(HouseErrorCode.HOUSE_ANALYSIS_FAILED));

        mockMvc.perform(multipart("/api/house-analyses").file(image))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.code").value("HOUSE_009"));
    }

    @Test
    @DisplayName("images 파트 자체가 없으면 프레임워크 예외 없이 Service 검증으로 흘러 400을 반환한다")
    void returnsBadRequestWhenImagesPartIsMissing() throws Exception {
        when(houseAnalysisService.analyze(any()))
                .thenThrow(new BusinessException(HouseErrorCode.INVALID_IMAGE_COUNT));

        mockMvc.perform(multipart("/api/house-analyses"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("HOUSE_006"));
    }

}
