package com.roommade.domain.house.client;

import java.util.List;
import java.util.Optional;

public interface TmapClient {

    /** 주소/장소 문자열로 지오코딩한다. 검색 결과가 없으면 빈 리스트를 반환한다. */
    List<TmapGeocodeCandidate> geocode(String query);

    /** 출발/도착 좌표 사이의 대중교통 예상 소요 시간(분)을 계산한다. 경로가 없으면 빈 Optional을 반환한다. */
    Optional<Integer> getTransitMinutes(double startLat, double startLon, double endLat, double endLon);
}
