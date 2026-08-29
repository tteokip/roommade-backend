package com.roommade.domain.house.dto.request;

import java.math.BigDecimal;
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
    private Integer commuteMinutes;

    @Size(max = 30)
    private String floorType;

    @Size(max = 30)
    private String roomStructure;

    @Size(max = 30)
    private String optionType;
}
