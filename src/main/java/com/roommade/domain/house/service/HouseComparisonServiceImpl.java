package com.roommade.domain.house.service;

import com.roommade.domain.house.dto.response.HouseComparisonCurrentResponse;
import com.roommade.domain.house.mapper.HouseComparisonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HouseComparisonServiceImpl implements HouseComparisonService {

    private final HouseComparisonMapper houseComparisonMapper;

    @Override
    public HouseComparisonCurrentResponse getCurrentComparison(Long userId) {
        HouseComparisonCurrentResponse response = houseComparisonMapper.findCurrentByUserId(userId);
        return response != null ? response : HouseComparisonCurrentResponse.noComparison();
    }
}
