package com.roommade.domain.living.service;

import com.roommade.domain.coin.service.CoinService;
import com.roommade.domain.living.code.LivingErrorCode;
import com.roommade.domain.living.dto.response.EmergencyFundResponse;
import com.roommade.domain.living.mapper.EmergencyFundMapper;
import com.roommade.global.exception.BusinessException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmergencyFundServiceImpl implements EmergencyFundService {

    private static final int ACHIEVEMENT_REWARD_COIN = 1000;

    private final EmergencyFundMapper emergencyFundMapper;
    private final CoinService coinService;

    @Override
    public EmergencyFundResponse getEmergencyFund(Long userId) {
        EmergencyFundResponse response = emergencyFundMapper.findByUserId(userId);
        return response != null ? response : EmergencyFundResponse.notStarted();
    }

    @Override
    @Transactional
    public EmergencyFundResponse setTarget(Long userId, Long targetAmount) {
        EmergencyFundResponse current = emergencyFundMapper.findByUserId(userId);
        boolean wasAlreadyAchieved = current != null && current.getAchievedAt() != null;

        LocalDateTime achievedAt;
        if (current == null) {
            achievedAt = resolveAchievedAt(null, 0L, targetAmount);
            try {
                emergencyFundMapper.insert(userId, targetAmount, achievedAt);
            } catch (DataIntegrityViolationException exception) {
                throw new BusinessException(LivingErrorCode.USER_NOT_FOUND);
            }
        } else {
            achievedAt = resolveAchievedAt(current.getAchievedAt(), current.getCurrentAmount(), targetAmount);
            emergencyFundMapper.updateTarget(userId, targetAmount, achievedAt);
        }

        if (!wasAlreadyAchieved && achievedAt != null) {
            coinService.earn(userId, ACHIEVEMENT_REWARD_COIN);
        }

        return emergencyFundMapper.findByUserId(userId);
    }

    /**
     * achieved_at은 "최초 달성 일시"이므로 한 번 채워지면 목표 금액이 나중에 올라가도 지우지 않는다.
     */
    private LocalDateTime resolveAchievedAt(
            LocalDateTime existingAchievedAt, Long currentAmount, Long targetAmount) {
        if (existingAchievedAt != null) {
            return existingAchievedAt;
        }
        return currentAmount >= targetAmount ? LocalDateTime.now() : null;
    }
}