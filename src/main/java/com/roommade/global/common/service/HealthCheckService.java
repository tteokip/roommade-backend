package com.roommade.global.common.service;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.roommade.global.common.mapper.HealthMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HealthCheckService {

    private final HealthMapper healthMapper;

    public boolean isDatabaseUp() {
        try {
            healthMapper.checkConnection();
            return true;
        } catch (DataAccessException e) {
            return false;
        }
    }
}
