package com.roommade.domain.house.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.roommade.domain.house.client.HouseImageAnalysisClient;
import com.roommade.domain.house.client.HouseImageExtraction;
import com.roommade.domain.house.code.HouseErrorCode;
import com.roommade.domain.house.dto.response.HouseAnalysisResponse;
import com.roommade.domain.house.dto.response.HouseAnalysisStatus;
import com.roommade.global.exception.BusinessException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class HouseAnalysisServiceImplTest {

    @Mock
    private HouseImageAnalysisClient houseImageAnalysisClient;

    @InjectMocks
    private HouseAnalysisServiceImpl houseAnalysisService;

    @Test
    @DisplayName("location/deposit/monthlyRent가 모두 있으면 COMPLETED를 반환하고 commuteMinutes는 항상 null이다")
    void returnsCompletedWhenRequiredFieldsArePresent() {
        List<MultipartFile> images = List.of(image("a.jpg", "image/jpeg"));
        when(houseImageAnalysisClient.analyze(images)).thenReturn(new HouseImageExtraction(
                Optional.of("서울시 광진구"), Optional.of(10_000_000L), Optional.of(500_000L), Optional.of(50_000L),
                Optional.of(new BigDecimal("23.10")), Optional.of(7), Optional.of("3층"), Optional.of("원룸"),
                Optional.of("풀옵션")));

        HouseAnalysisResponse response = houseAnalysisService.analyze(images);

        assertThat(response.getAnalysisStatus()).isEqualTo(HouseAnalysisStatus.COMPLETED);
        assertThat(response.getHouse().getLocation()).isEqualTo("서울시 광진구");
        assertThat(response.getHouse().getDeposit()).isEqualTo(10_000_000L);
        assertThat(response.getHouse().getMonthlyRent()).isEqualTo(500_000L);
        assertThat(response.getHouse().getCommuteMinutes()).isNull();
    }

    @Test
    @DisplayName("location/deposit/monthlyRent 중 하나라도 없으면 PARTIAL을 반환한다")
    void returnsPartialWhenAnyRequiredFieldIsMissing() {
        List<MultipartFile> images = List.of(image("a.jpg", "image/jpeg"));
        when(houseImageAnalysisClient.analyze(images)).thenReturn(new HouseImageExtraction(
                Optional.of("서울시 광진구"), Optional.empty(), Optional.of(500_000L), Optional.of(50_000L),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));

        HouseAnalysisResponse response = houseAnalysisService.analyze(images);

        assertThat(response.getAnalysisStatus()).isEqualTo(HouseAnalysisStatus.PARTIAL);
    }

    @Test
    @DisplayName("location이 공백이면 PARTIAL을 반환한다")
    void returnsPartialWhenLocationIsBlank() {
        List<MultipartFile> images = List.of(image("a.jpg", "image/jpeg"));
        when(houseImageAnalysisClient.analyze(images)).thenReturn(new HouseImageExtraction(
                Optional.of("  "), Optional.of(10_000_000L), Optional.of(500_000L), Optional.of(50_000L),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));

        HouseAnalysisResponse response = houseAnalysisService.analyze(images);

        assertThat(response.getAnalysisStatus()).isEqualTo(HouseAnalysisStatus.PARTIAL);
    }

    @Test
    @DisplayName("선택 필드만 누락되면 PARTIAL로 판단하지 않는다")
    void doesNotTreatMissingOptionalFieldsAsPartial() {
        List<MultipartFile> images = List.of(image("a.jpg", "image/jpeg"));
        when(houseImageAnalysisClient.analyze(images)).thenReturn(new HouseImageExtraction(
                Optional.of("서울시 광진구"), Optional.of(10_000_000L), Optional.of(500_000L), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));

        HouseAnalysisResponse response = houseAnalysisService.analyze(images);

        assertThat(response.getAnalysisStatus()).isEqualTo(HouseAnalysisStatus.COMPLETED);
    }

    @Test
    @DisplayName("이미지가 0장이면 클라이언트를 호출하지 않고 INVALID_IMAGE_COUNT 예외를 던진다")
    void throwsWhenNoImagesProvided() {
        assertThatThrownBy(() -> houseAnalysisService.analyze(List.of()))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(HouseErrorCode.INVALID_IMAGE_COUNT);

        verifyNoInteractions(houseImageAnalysisClient);
    }

    @Test
    @DisplayName("이미지가 4장이면 INVALID_IMAGE_COUNT 예외를 던진다")
    void throwsWhenTooManyImagesProvided() {
        List<MultipartFile> images = List.of(
                image("a.jpg", "image/jpeg"), image("b.jpg", "image/jpeg"),
                image("c.jpg", "image/jpeg"), image("d.jpg", "image/jpeg"));

        assertThatThrownBy(() -> houseAnalysisService.analyze(images))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(HouseErrorCode.INVALID_IMAGE_COUNT);

        verifyNoInteractions(houseImageAnalysisClient);
    }

    @Test
    @DisplayName("빈 파일이 섞여 있으면 EMPTY_IMAGE_FILE 예외를 던진다")
    void throwsWhenAnyImageIsEmpty() {
        MockMultipartFile empty = new MockMultipartFile("images", "empty.jpg", "image/jpeg", new byte[0]);

        assertThatThrownBy(() -> houseAnalysisService.analyze(List.of(image("a.jpg", "image/jpeg"), empty)))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(HouseErrorCode.EMPTY_IMAGE_FILE);

        verifyNoInteractions(houseImageAnalysisClient);
    }

    @Test
    @DisplayName("지원하지 않는 형식이 섞여 있으면 UNSUPPORTED_IMAGE_FORMAT 예외를 던진다")
    void throwsWhenImageFormatIsUnsupported() {
        List<MultipartFile> images = List.of(image("a.gif", "image/gif"));

        assertThatThrownBy(() -> houseAnalysisService.analyze(images))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(HouseErrorCode.UNSUPPORTED_IMAGE_FORMAT);

        verifyNoInteractions(houseImageAnalysisClient);
    }

    private MockMultipartFile image(String filename, String contentType) {
        return new MockMultipartFile("images", filename, contentType, new byte[]{1, 2, 3});
    }
}
