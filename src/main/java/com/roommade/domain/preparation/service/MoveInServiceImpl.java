package com.roommade.domain.preparation.service;

import com.roommade.domain.house.code.HouseErrorCode;
import com.roommade.domain.house.mapper.HouseComparisonMapper;
import com.roommade.domain.preparation.code.PreparationErrorCode;
import com.roommade.domain.preparation.dto.request.MoveInConfirmRequest;
import com.roommade.domain.preparation.dto.request.MoveInConfirmRequest.ConfirmationType;
import com.roommade.domain.preparation.dto.response.IndependenceStatus;
import com.roommade.domain.preparation.dto.response.MoveInStateSourceResponse;
import com.roommade.domain.preparation.dto.response.MoveInConfirmationResponse;
import com.roommade.global.exception.BusinessException;
import java.time.Clock;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MoveInServiceImpl implements MoveInService {

    private final HouseComparisonMapper houseComparisonMapper;
    private final PreparationService preparationService;
    private final Clock clock;

    @Override
    @Transactional
    public MoveInConfirmationResponse confirmMoveIn(
            Long userId, MoveInConfirmRequest request) {
        validateMoveInDate(request);
        Long confirmedHouseId = resolveConfirmedHouseId(userId, request);
        MoveInStateSourceResponse state = preparationService.scheduleMoveIn(
                userId, confirmedHouseId, request.getMoveInDate());

        return new MoveInConfirmationResponse(
                confirmedHouseId,
                confirmedHouseId == null,
                state.getMoveInDate(),
                state.getMovedInAt(),
                IndependenceStatus.from(state.getMoveInDate(), state.getMovedInAt()));
    }

    private void validateMoveInDate(MoveInConfirmRequest request) {
        if (request == null || request.getMoveInDate() == null) {
            throw new BusinessException(PreparationErrorCode.INVALID_MOVE_IN_CONFIRMATION);
        }
        if (request.getMoveInDate().isBefore(LocalDate.now(clock))) {
            throw new BusinessException(PreparationErrorCode.MOVE_IN_DATE_IN_PAST);
        }
    }

    private Long resolveConfirmedHouseId(Long userId, MoveInConfirmRequest request) {
        if (request.getConfirmationType() == null) {
            throw new BusinessException(PreparationErrorCode.INVALID_MOVE_IN_CONFIRMATION);
        }

        if (request.getConfirmationType() == ConfirmationType.OTHER) {
            if (request.getHouseId() != null) {
                throw new BusinessException(PreparationErrorCode.INVALID_MOVE_IN_CONFIRMATION);
            }
            return null;
        }

        Long houseId = request.getHouseId();
        if (houseId == null) {
            throw new BusinessException(PreparationErrorCode.INVALID_MOVE_IN_CONFIRMATION);
        }
        if (!houseComparisonMapper.existsHouseByIdAndUserId(houseId, userId)) {
            throw new BusinessException(HouseErrorCode.HOUSE_NOT_CONFIRMABLE);
        }
        return houseId;
    }
}
