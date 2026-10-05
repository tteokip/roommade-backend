package com.roommade.domain.preparation.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 입주 예정일과 실제 독립 이후 전환 시각 조회 결과. */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MoveInStateSourceResponse {

    private LocalDate moveInDate;
    private LocalDateTime movedInAt;
}
