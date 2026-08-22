package com.roommade.global.common.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.roommade.global.common.service.HealthCheckService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class HealthController {

    private final HealthCheckService healthCheckService;

    @GetMapping("/health/db")
    public ResponseEntity<Map<String, String>> healthDb() {
        boolean databaseUp = healthCheckService.isDatabaseUp();

        Map<String, String> response = new LinkedHashMap<>();
        response.put("status", databaseUp ? "UP" : "DOWN");
        response.put("database", databaseUp ? "UP" : "DOWN");

        HttpStatus httpStatus = databaseUp ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
        return ResponseEntity.status(httpStatus).body(response);
    }
}
