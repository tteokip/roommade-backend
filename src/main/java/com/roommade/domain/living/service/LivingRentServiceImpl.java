package com.roommade.domain.living.service;

import com.roommade.domain.living.code.LivingErrorCode;
import com.roommade.domain.living.dto.response.LivingRentResponse;
import com.roommade.domain.living.mapper.LivingRentMapper;
import com.roommade.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LivingRentServiceImpl implements LivingRentService {

    private final LivingRentMapper livingRentMapper;

    @Override
    @Transactional
    public LivingRentResponse setMonthlyRent(Long userId, Long monthlyRent) {
        LivingRentResponse current = livingRentMapper.findByUserId(userId);

        if (current == null) {
            try {
                livingRentMapper.insert(userId, monthlyRent);
            } catch (DataIntegrityViolationException exception) {
                throw new BusinessException(LivingErrorCode.USER_NOT_FOUND);
            }
        } else {
            livingRentMapper.updateRent(userId, monthlyRent);
        }

        return livingRentMapper.findByUserId(userId);
    }
}
