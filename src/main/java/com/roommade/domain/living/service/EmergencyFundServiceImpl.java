package com.roommade.domain.living.service;

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

    private final EmergencyFundMapper emergencyFundMapper;

    @Override
    public EmergencyFundResponse getEmergencyFund(Long userId) {
        EmergencyFundResponse response = emergencyFundMapper.findByUserId(userId);
        return response != null ? response : EmergencyFundResponse.notStarted();
    }

    @Override
    @Transactional
    public EmergencyFundResponse setTarget(Long userId, Long targetAmount) {
        EmergencyFundResponse current = emergencyFundMapper.findByUserId(userId);

        if (current == null) {
            LocalDateTime achievedAt = resolveAchievedAt(null, 0L, targetAmount);
            try {
                emergencyFundMapper.insert(userId, targetAmount, achievedAt);
            } catch (DataIntegrityViolationException exception) {
                throw new BusinessException(LivingErrorCode.USER_NOT_FOUND);
            }
        } else {
            LocalDateTime achievedAt =
                    resolveAchievedAt(current.getAchievedAt(), current.getCurrentAmount(), targetAmount);
            emergencyFundMapper.updateTarget(userId, targetAmount, achievedAt);
        }

        return emergencyFundMapper.findByUserId(userId);
    }

    @Override
    @Transactional
    public EmergencyFundResponse updateCurrentAmount(Long userId, Long currentAmount) {
        EmergencyFundResponse current = emergencyFundMapper.findByUserId(userId);
        if (current == null) {
            throw new BusinessException(LivingErrorCode.EMERGENCY_FUND_NOT_SET);
        }

        LocalDateTime achievedAt =
                resolveAchievedAt(current.getAchievedAt(), currentAmount, current.getTargetAmount());
        emergencyFundMapper.updateCurrentAmount(userId, currentAmount, achievedAt);

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