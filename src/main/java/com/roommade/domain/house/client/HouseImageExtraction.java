package com.roommade.domain.house.client;

import java.math.BigDecimal;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** OpenAI 구조화 출력에서 누락 가능한 필드를 표현하기 위해 {@link Optional}을 사용한다. */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HouseImageExtraction {

    private Optional<String> location;
    private Optional<Long> deposit;
    private Optional<Long> monthlyRent;
    private Optional<Long> maintenanceFee;
    private Optional<BigDecimal> area;
    private Optional<Integer> stationWalkMinutes;
    private Optional<String> floorType;
    private Optional<String> roomStructure;
    private Optional<String> optionType;
}
