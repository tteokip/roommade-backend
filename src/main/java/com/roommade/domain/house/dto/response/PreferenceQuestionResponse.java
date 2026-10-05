package com.roommade.domain.house.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PreferenceQuestionResponse {

    private Long id;
    private String optionAText;
    private ComparisonFactor optionAFactor;
    private String optionBText;
    private ComparisonFactor optionBFactor;
}
