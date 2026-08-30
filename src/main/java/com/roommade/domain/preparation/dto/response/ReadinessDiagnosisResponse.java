package com.roommade.domain.preparation.dto.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReadinessDiagnosisResponse {

    private BigDecimal readinessScore;
    private Integer maxScore;
    private BigDecimal rirScore;
    private Integer rirMaxScore;
    private BigDecimal depositScore;
    private Integer depositMaxScore;
    private Integer houseComparisonScore;
    private Integer houseComparisonMaxScore;
}
