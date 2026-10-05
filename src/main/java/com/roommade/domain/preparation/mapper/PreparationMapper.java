package com.roommade.domain.preparation.mapper;

import com.roommade.domain.preparation.dto.response.DepositProgressSourceResponse;
import com.roommade.domain.preparation.dto.response.MoveInStateSourceResponse;
import com.roommade.domain.preparation.dto.response.RirProfileResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PreparationMapper {

    /** 사용자 RIR 계산용 월 소득과 월세 상한을 원 단위로 조회. */
    RirProfileResponse findRirProfileByUserId(@Param("userId") Long userId);

    /** 사용자 보증금 계산용 목표 금액과 현재 마련 금액 조회. */
    DepositProgressSourceResponse findDepositProgressByUserId(@Param("userId") Long userId);

    /** 사용자 최초 비교 매물 등록 완료 시간 조회. */
    LocalDateTime findHouseComparisonCompletedAtByUserId(@Param("userId") Long userId);

    /** 사용자 입주 예정일과 독립 이후 전환 시간 조회. */
    MoveInStateSourceResponse findMoveInStateByUserId(@Param("userId") Long userId);

    /** 사용자 자립 준비 진행 데이터 존재 여부 조회. */
    boolean existsIndependenceProgressByUserId(@Param("userId") Long userId);

    /** 사용자 최초 비교 매물 등록 완료 시간 기록. */
    int markHouseComparisonCompleted(@Param("userId") Long userId);

    /** 입주 확정 시 매물과 입주일을 저장하고, 오늘 입주인 경우에만 실제 전환 시간도 함께 기록. */
    int updateMoveInSchedule(
            @Param("userId") Long userId,
            @Param("confirmedHouseId") Long confirmedHouseId,
            @Param("moveInDate") LocalDate moveInDate,
            @Param("movedInAt") LocalDateTime movedInAt);

    /** 입주일이 오늘 또는 그 이전이면서 실제 전환 시간이 없는 사용자의 전환 시간을 일괄 기록. */
    int updateDueMoveIns(
            @Param("today") LocalDate today,
            @Param("movedInAt") LocalDateTime movedInAt);
}
