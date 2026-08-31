package com.roommade.domain.house.client;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TmapGeocodeCandidate {

    private double lat;
    private double lon;
    private String cityDo;
    private String guGun;
    private String legalDong;
    private String adminDong;

    public TmapGeocodeCandidate(double lat, double lon, String cityDo, String guGun) {
        this(lat, lon, cityDo, guGun, null, null);
    }

    public TmapGeocodeCandidate(
            double lat, double lon, String cityDo, String guGun, String legalDong, String adminDong) {
        this.lat = lat;
        this.lon = lon;
        this.cityDo = cityDo;
        this.guGun = guGun;
        this.legalDong = legalDong;
        this.adminDong = adminDong;
    }
}
