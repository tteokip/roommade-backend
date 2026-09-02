package com.roommade.domain.preparation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate moveInDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime movedInAt;

    private IndependenceStatus independenceStatus;
}
