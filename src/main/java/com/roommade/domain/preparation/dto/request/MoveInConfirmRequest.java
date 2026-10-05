package com.roommade.domain.preparation.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MoveInConfirmRequest {

    public enum ConfirmationType {
        COMPARISON,
        OTHER
    }

    @NotNull
    private ConfirmationType confirmationType;

    private Long houseId;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate moveInDate;
}
