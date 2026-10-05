package com.roommade.domain.preparation.controller;

import com.roommade.domain.preparation.code.PreparationSuccessCode;
import com.roommade.domain.preparation.dto.request.MoveInConfirmRequest;
import com.roommade.domain.preparation.dto.response.MoveInConfirmationResponse;
import com.roommade.domain.preparation.service.MoveInService;
import com.roommade.global.response.ApiResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/move-ins")
public class MoveInController {

    private final MoveInService moveInService;

    /** 사용자의 입주 매물과 입주 예정일 확정. */
    @PostMapping
    public ResponseEntity<ApiResponse<MoveInConfirmationResponse>> confirmMoveIn(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody MoveInConfirmRequest request) {
        MoveInConfirmationResponse response = moveInService.confirmMoveIn(userId, request);
        return ResponseEntity.status(PreparationSuccessCode.MOVE_IN_CONFIRMED.getStatus())
                .body(ApiResponse.success(PreparationSuccessCode.MOVE_IN_CONFIRMED, response));
    }
}
