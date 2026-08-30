package com.roommade.domain.house.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BalanceGameAnswerRequest {

    @NotBlank
    @Pattern(regexp = "A|B", message = "selectedSide는 A 또는 B여야 합니다.")
    private String selectedSide;
}
