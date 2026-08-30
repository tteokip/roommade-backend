package com.roommade.domain.preparation.mapper;

import com.roommade.domain.preparation.dto.response.DepositProgressSourceResponse;
import com.roommade.domain.preparation.dto.response.RirProfileResponse;
import java.time.LocalDateTime;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PreparationMapper {

    /** 사용자 RIR 계산용 월 소득과 월세 상한 조회. */
    RirProfileResponse findRirProfileByUserId(@Param("userId") Long userId);

    /** 사용자 보증금 계산용 목표 금액과 현재 마련 금액 조회. */
    DepositProgressSourceResponse findDepositProgressByUserId(@Param("userId") Long userId);

    /** 사용자 최초 비교 매물 등록 완료 시간 조회. */
    LocalDateTime findHouseComparisonCompletedAtByUserId(@Param("userId") Long userId);

    /** 사용자 집 확정 완료 시간 조회. */
    LocalDateTime findHouseConfirmedAtByUserId(@Param("userId") Long userId);

    /** 사용자 자립 준비 진행 데이터 존재 여부 조회. */
    boolean existsIndependenceProgressByUserId(@Param("userId") Long userId);

    /** 사용자 최초 비교 매물 등록 완료 시간 기록. */
    int markHouseComparisonCompleted(@Param("userId") Long userId);

    /** 사용자 집 확정 매물 및 완료 시간 기록. */
    int updateHouseConfirmation(
            @Param("userId") Long userId,
            @Param("confirmedHouseId") Long confirmedHouseId);
}
