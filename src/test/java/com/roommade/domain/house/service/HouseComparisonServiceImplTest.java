package com.roommade.domain.house.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.roommade.domain.house.code.HouseErrorCode;
import com.roommade.domain.house.dto.request.HouseRegisterRequest;
import com.roommade.domain.house.dto.response.HouseComparisonCurrentResponse;
import com.roommade.domain.house.mapper.HouseComparisonMapper;
import com.roommade.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

@ExtendWith(MockitoExtension.class)
class HouseComparisonServiceImplTest {

    @Mock
    private HouseComparisonMapper houseComparisonMapper;

    @InjectMocks
    private HouseComparisonServiceImpl houseComparisonService;

    @Test
    @DisplayName("Mapper 결과가 있으면 가공 없이 그대로 반환하고, Mapper는 해당 userId로 한 번 호출된다")
    void returnsMapperResultWhenComparisonExists() {
        Long userId = 1L;
        HouseComparisonCurrentResponse mapperResult =
                new HouseComparisonCurrentResponse(100L, null, null, false);
        when(houseComparisonMapper.findCurrentByUserId(userId)).thenReturn(mapperResult);

        HouseComparisonCurrentResponse result = houseComparisonService.getCurrentComparison(userId);

        assertThat(result).isSameAs(mapperResult);
        verify(houseComparisonMapper).findCurrentByUserId(userId);
    }

    @Test
    @DisplayName("Mapper가 null을 반환하면 comparisonId/houseA/houseB가 null이고 balanceGameAvailable이 false인 빈 응답을 반환한다")
    void returnsEmptyResponseWhenMapperReturnsNull() {
        Long userId = 1L;
        when(houseComparisonMapper.findCurrentByUserId(userId)).thenReturn(null);

        HouseComparisonCurrentResponse result = houseComparisonService.getCurrentComparison(userId);

        assertThat(result.getComparisonId()).isNull();
        assertThat(result.getHouseA()).isNull();
        assertThat(result.getHouseB()).isNull();
        assertThat(result.isBalanceGameAvailable()).isFalse();
    }

    @Test
    @DisplayName("비교가 없으면 house_comparisons를 생성한 뒤 매물을 등록하고, 재조회한 최신 상태를 반환한다")
    void createsComparisonWhenNoneExistsThenRegistersHouse() {
        Long userId = 1L;
        HouseRegisterRequest request = new HouseRegisterRequest(
                "서울시 강남구", 100_000_000L, 500_000L, null, null, null, null, null, null, null);
        HouseComparisonCurrentResponse afterCreate = new HouseComparisonCurrentResponse(100L, null, null, false);
        HouseComparisonCurrentResponse finalResponse = new HouseComparisonCurrentResponse(100L, null, null, false);

        when(houseComparisonMapper.findCurrentByUserId(userId))
                .thenReturn(null)
                .thenReturn(afterCreate)
                .thenReturn(finalResponse);

        HouseComparisonCurrentResponse result = houseComparisonService.registerHouse(userId, "A", request);

        assertThat(result).isSameAs(finalResponse);
        verify(houseComparisonMapper).insertComparison(userId);
        verify(houseComparisonMapper).insertHouse(100L, "A", request);
    }

    @Test
    @DisplayName("비교가 이미 있으면 새로 만들지 않고 바로 매물을 등록한다")
    void registersHouseWithoutCreatingComparisonWhenOneExists() {
        Long userId = 1L;
        HouseRegisterRequest request = new HouseRegisterRequest(
                "서울시 마포구", 20_000L, 60L, null, null, null, null, null, null, null);
        HouseComparisonCurrentResponse existing = new HouseComparisonCurrentResponse(200L, null, null, false);
        HouseComparisonCurrentResponse finalResponse = new HouseComparisonCurrentResponse(200L, null, null, true);

        when(houseComparisonMapper.findCurrentByUserId(userId))
                .thenReturn(existing)
                .thenReturn(finalResponse);

        HouseComparisonCurrentResponse result = houseComparisonService.registerHouse(userId, "B", request);

        assertThat(result).isSameAs(finalResponse);
        verify(houseComparisonMapper, never()).insertComparison(any());
        verify(houseComparisonMapper).insertHouse(200L, "B", request);
    }

    @Test
    @DisplayName("houseType이 A/B가 아니면 Mapper를 전혀 호출하지 않고 예외를 던진다")
    void throwsExceptionWhenHouseTypeIsInvalid() {
        Long userId = 1L;
        HouseRegisterRequest request = new HouseRegisterRequest(
                "서울시", 10_000_000L, 100_000L, null, null, null, null, null, null, null);

        assertThatThrownBy(() -> houseComparisonService.registerHouse(userId, "C", request))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(HouseErrorCode.INVALID_HOUSE_TYPE);

        verifyNoInteractions(houseComparisonMapper);
    }

    @Test
    @DisplayName("이미 등록된 슬롯에 다시 등록하면 HOUSE_SLOT_ALREADY_OCCUPIED로 변환한다")
    void throwsHouseSlotAlreadyOccupiedWhenInsertHouseViolatesUniqueConstraint() {
        Long userId = 1L;
        HouseRegisterRequest request = new HouseRegisterRequest(
                "서울시", 10_000_000L, 100_000L, null, null, null, null, null, null, null);
        HouseComparisonCurrentResponse existing = new HouseComparisonCurrentResponse(300L, null, null, false);

        when(houseComparisonMapper.findCurrentByUserId(userId)).thenReturn(existing);
        doThrow(new DuplicateKeyException("duplicate"))
                .when(houseComparisonMapper).insertHouse(300L, "A", request);

        assertThatThrownBy(() -> houseComparisonService.registerHouse(userId, "A", request))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(HouseErrorCode.HOUSE_SLOT_ALREADY_OCCUPIED);
    }

}
