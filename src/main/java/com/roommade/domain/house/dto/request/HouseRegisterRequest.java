package com.roommade.domain.house.dto.request;

import java.math.BigDecimal;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HouseRegisterRequest {

    @NotBlank
    @Size(max = 255)
    private String location;

    @NotNull
    @PositiveOrZero
    private Long deposit;

    @NotNull
    @PositiveOrZero
    private Long monthlyRent;

    @PositiveOrZero
    private Long maintenanceFee;

    @Positive
    @Digits(integer = 6, fraction = 2)
    private BigDecimal area;

    @PositiveOrZero
    private Integer stationWalkMinutes;

    @PositiveOrZero
    private Integer commuteMinMinutes;

    @PositiveOrZero
    private Integer commuteMaxMinutes;

    @Size(max = 30)
    private String floorType;

    @Size(max = 30)
    private String roomStructure;

    @Size(max = 30)
    private String optionType;

    /**
     * 통근 시간 계산 API 응답을 그대로 넘겨받는 값이라 등록 시점에는 재계산하지 않고, 둘 다
     * 있거나 둘 다 없는지 그리고 min &lt;= max인지만 검증한다.
     */
    @AssertTrue(message = "commuteMinMinutes와 commuteMaxMinutes는 함께 입력하거나 함께 비워야 하고, min은 max보다 클 수 없습니다.")
    private boolean isCommuteRangeValid() {
        if (commuteMinMinutes == null && commuteMaxMinutes == null) {
            return true;
        }
        if (commuteMinMinutes == null || commuteMaxMinutes == null) {
            return false;
        }
        return commuteMinMinutes <= commuteMaxMinutes;
    }
}
