package com.roommade.domain.house.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PreferenceAnswerResponse {

    private Long questionId;
    private String selectedSide;
}
