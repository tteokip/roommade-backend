package com.roommade.domain.preparation.service;

import com.roommade.domain.preparation.dto.response.DepositProgressResponse;
import com.roommade.domain.preparation.dto.response.HouseComparisonProgressResponse;
import com.roommade.domain.preparation.dto.response.MoveInStateSourceResponse;
import com.roommade.domain.preparation.dto.response.ReadinessDiagnosisResponse;
import com.roommade.domain.preparation.dto.response.RirDiagnosisResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface PreparationService {

    /** 사용자 월 소득과 예상 월세 기반 RIR 진단 결과 조회. */
    RirDiagnosisResponse getRirDiagnosis(Long userId);

    /** 사용자 목표 보증금과 현재 마련 금액 기반 보증금 현황 조회. */
    DepositProgressResponse getDepositProgress(Long userId);

    /** 사용자 집 비교 점수 조회. */
    HouseComparisonProgressResponse getHouseComparisonProgress(Long userId);

    /** 사용자 자립 준비도 전체 진단 결과 조회. */
    ReadinessDiagnosisResponse getReadinessDiagnosis(Long userId);

    /** 사용자 최초 비교 매물 등록 완료 기록. */
    void markHouseComparisonCompleted(Long userId);

    /** 기존 집 확정 API와의 하위 호환을 위해 오늘 입주로 즉시 전환. */
    LocalDateTime confirmHouse(Long userId, Long confirmedHouseId);

    /** 입주일을 저장하고, 오늘 입주면 즉시 전환하며 미래 입주면 예정 상태로 유지. */
    MoveInStateSourceResponse scheduleMoveIn(
            Long userId, Long confirmedHouseId, LocalDate moveInDate);

    /** 입주일이 도래했지만 아직 전환되지 않은 사용자를 독립 이후 상태로 일괄 전환. */
    int transitionDueMoveIns();
}
