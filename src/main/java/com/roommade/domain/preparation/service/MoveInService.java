package com.roommade.domain.preparation.service;

import com.roommade.domain.preparation.dto.request.MoveInConfirmRequest;
import com.roommade.domain.preparation.dto.response.MoveInConfirmationResponse;

public interface MoveInService {

    /** 사용자의 입주 확정 요청을 검증하고 입주 예정 상태를 생성. */
    MoveInConfirmationResponse confirmMoveIn(Long userId, MoveInConfirmRequest request);
}
