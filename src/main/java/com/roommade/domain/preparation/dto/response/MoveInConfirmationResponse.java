package com.roommade.domain.preparation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MoveInConfirmationResponse {

    private Long confirmedHouseId;
    private boolean manualRentInputRequired;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate moveInDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime movedInAt;

    private IndependenceStatus independenceStatus;
}
