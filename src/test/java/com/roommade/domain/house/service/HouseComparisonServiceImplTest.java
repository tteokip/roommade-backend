package com.roommade.domain.house.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.roommade.domain.house.dto.response.HouseComparisonCurrentResponse;
import com.roommade.domain.house.mapper.HouseComparisonMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
}
