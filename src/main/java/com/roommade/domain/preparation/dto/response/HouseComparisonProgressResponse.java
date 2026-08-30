package com.roommade.domain.preparation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HouseComparisonProgressResponse {

    private Integer houseComparisonScore;
    private Integer maxScore;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime houseComparisonCompletedAt;
}
