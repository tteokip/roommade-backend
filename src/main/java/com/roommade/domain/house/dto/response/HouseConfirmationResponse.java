package com.roommade.domain.house.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HouseConfirmationResponse {

    private Long confirmedHouseId;
    private boolean manualRentInputRequired;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime houseConfirmedAt;
}
