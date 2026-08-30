package com.roommade.domain.house.service;

import com.roommade.domain.house.code.HouseErrorCode;
import com.roommade.domain.house.dto.request.HouseConfirmRequest;
import com.roommade.domain.house.dto.request.HouseConfirmRequest.ConfirmationType;
import com.roommade.domain.house.dto.response.HouseConfirmationResponse;
import com.roommade.domain.house.mapper.HouseComparisonMapper;
import com.roommade.domain.preparation.service.PreparationService;
import com.roommade.global.exception.BusinessException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HouseConfirmationServiceImpl implements HouseConfirmationService {

    private final HouseComparisonMapper houseComparisonMapper;
    private final PreparationService preparationService;

    @Override
    @Transactional
    public HouseConfirmationResponse confirmHouse(Long userId, HouseConfirmRequest request) {
        Long confirmedHouseId = resolveConfirmedHouseId(userId, request);
        LocalDateTime houseConfirmedAt =
                preparationService.confirmHouse(userId, confirmedHouseId);

        return new HouseConfirmationResponse(
                confirmedHouseId,
                confirmedHouseId == null,
                houseConfirmedAt);
    }

    private Long resolveConfirmedHouseId(Long userId, HouseConfirmRequest request) {
        if (request == null || request.getConfirmationType() == null) {
            throw new BusinessException(HouseErrorCode.INVALID_HOUSE_CONFIRMATION);
        }

        if (request.getConfirmationType() == ConfirmationType.OTHER) {
            if (request.getHouseId() != null) {
                throw new BusinessException(HouseErrorCode.INVALID_HOUSE_CONFIRMATION);
            }
            return null;
        }

        Long houseId = request.getHouseId();
        if (houseId == null) {
            throw new BusinessException(HouseErrorCode.INVALID_HOUSE_CONFIRMATION);
        }
        if (!houseComparisonMapper.existsHouseByIdAndUserId(houseId, userId)) {
            throw new BusinessException(HouseErrorCode.HOUSE_NOT_CONFIRMABLE);
        }
        return houseId;
    }
}
