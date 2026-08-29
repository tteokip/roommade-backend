package com.roommade.domain.house.service;

import com.roommade.domain.house.code.HouseErrorCode;
import com.roommade.domain.house.dto.request.HouseRegisterRequest;
import com.roommade.domain.house.dto.response.HouseComparisonCurrentResponse;
import com.roommade.domain.house.mapper.HouseComparisonMapper;
import com.roommade.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HouseComparisonServiceImpl implements HouseComparisonService {

    private static final String HOUSE_TYPE_A = "A";
    private static final String HOUSE_TYPE_B = "B";

    private final HouseComparisonMapper houseComparisonMapper;

    @Override
    public HouseComparisonCurrentResponse getCurrentComparison(Long userId) {
        HouseComparisonCurrentResponse response = houseComparisonMapper.findCurrentByUserId(userId);
        return response != null ? response : HouseComparisonCurrentResponse.noComparison();
    }

    @Override
    @Transactional
    public HouseComparisonCurrentResponse registerHouse(
            Long userId, String houseType, HouseRegisterRequest request) {
        validateHouseType(houseType);

        Long comparisonId = resolveComparisonId(userId);

        try {
            houseComparisonMapper.insertHouse(comparisonId, houseType, request);
        } catch (DuplicateKeyException e) {
            // (comparison_id, house_type) UNIQUE 위반 — 이미 등록된 슬롯이다.
            throw new BusinessException(HouseErrorCode.HOUSE_SLOT_ALREADY_OCCUPIED);
        }

        return houseComparisonMapper.findCurrentByUserId(userId);
    }

    private void validateHouseType(String houseType) {
        if (!HOUSE_TYPE_A.equals(houseType) && !HOUSE_TYPE_B.equals(houseType)) {
            throw new BusinessException(HouseErrorCode.INVALID_HOUSE_TYPE);
        }
    }

    private Long resolveComparisonId(Long userId) {
        HouseComparisonCurrentResponse current = houseComparisonMapper.findCurrentByUserId(userId);
        if (current != null) {
            return current.getComparisonId();
        }

        houseComparisonMapper.insertComparison(userId);

        return houseComparisonMapper.findCurrentByUserId(userId).getComparisonId();
    }
}
