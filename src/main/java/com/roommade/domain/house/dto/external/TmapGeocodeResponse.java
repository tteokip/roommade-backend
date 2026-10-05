package com.roommade.domain.house.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmapGeocodeResponse {

    private CoordinateInfo coordinateInfo;

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CoordinateInfo {
        private List<Coordinate> coordinate = List.of();
    }

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Coordinate {
        @JsonProperty("city_do")
        private String cityDo;
        @JsonProperty("gu_gun")
        private String guGun;
        private String legalDong;
        private String adminDong;
        private String lat;
        private String lon;
        private String newLat;
        private String newLon;
    }
}
