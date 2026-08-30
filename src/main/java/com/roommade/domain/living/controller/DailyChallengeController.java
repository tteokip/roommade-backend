package com.roommade.domain.living.controller;

import com.roommade.domain.living.code.LivingErrorCode;
import com.roommade.domain.living.code.LivingSuccessCode;
import com.roommade.domain.living.dto.response.DailyChallengeResponse;
import com.roommade.domain.living.service.DailyChallengeService;
import com.roommade.global.exception.BusinessException;
import com.roommade.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증이 아직 없어, 사용자 식별을 필수 헤더 {@code X-User-Id}로 임시 대체한다(house 도메인과 동일한 방식).
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/living")
public class DailyChallengeController {

    private final DailyChallengeService dailyChallengeService;

    @GetMapping("/daily-challenges")
    public ApiResponse<DailyChallengeResponse> getDailyChallenge(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        validateUserId(userId);
        DailyChallengeResponse response = dailyChallengeService.getDailyChallenge(userId);
        return ApiResponse.success(LivingSuccessCode.DAILY_CHALLENGE_FOUND, response);
    }

    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new BusinessException(LivingErrorCode.USER_ID_REQUIRED);
        }
    }
}
