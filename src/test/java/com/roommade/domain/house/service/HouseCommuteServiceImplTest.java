package com.roommade.domain.house.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.roommade.domain.house.client.TmapClient;
import com.roommade.domain.house.client.TmapGeocodeCandidate;
import com.roommade.domain.house.code.HouseErrorCode;
import com.roommade.domain.house.dto.response.HouseCommuteEstimateResponse;
import com.roommade.domain.house.dto.response.WorkplaceAddressResponse;
import com.roommade.domain.house.mapper.HouseCommuteMapper;
import com.roommade.global.exception.BusinessException;
import com.roommade.global.exception.CommonErrorCode;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HouseCommuteServiceImplTest {

    private static final Long USER_ID = 1L;
    private static final String LOCATION = "서울시 송파구 방이동";
    private static final String WORKPLACE_ROAD_ADDRESS = "서울특별시 중구 을지로 65";
    private static final TmapGeocodeCandidate WORKPLACE_CANDIDATE =
            new TmapGeocodeCandidate(37.56649, 126.985121, "서울", "중구");

    @Mock
    private HouseCommuteMapper houseCommuteMapper;

    @Mock
    private TmapClient tmapClient;

    @InjectMocks
    private HouseCommuteServiceImpl houseCommuteService;

    @Test
    @DisplayName("location이 공백이면 어떤 의존성도 호출하지 않고 실패한다")
    void throwsWhenLocationIsBlank() {
        assertThatThrownBy(() -> houseCommuteService.estimate(USER_ID, "  "))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(CommonErrorCode.INVALID_REQUEST);

        verifyNoInteractions(houseCommuteMapper, tmapClient);
    }

    @Test
    @DisplayName("직장 주소를 등록한 user_profiles 행이 없으면 WORKPLACE_ADDRESS_NOT_SET을 던진다")
    void throwsWhenUserProfileDoesNotExist() {
        when(houseCommuteMapper.findWorkplaceAddressByUserId(USER_ID)).thenReturn(null);

        assertThatThrownBy(() -> houseCommuteService.estimate(USER_ID, LOCATION))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(HouseErrorCode.WORKPLACE_ADDRESS_NOT_SET);

        verifyNoInteractions(tmapClient);
    }

    @Test
    @DisplayName("workplace_road_address가 비어 있으면 WORKPLACE_ADDRESS_NOT_SET을 던진다")
    void throwsWhenWorkplaceRoadAddressIsBlank() {
        when(houseCommuteMapper.findWorkplaceAddressByUserId(USER_ID))
                .thenReturn(new WorkplaceAddressResponse("  "));

        assertThatThrownBy(() -> houseCommuteService.estimate(USER_ID, LOCATION))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(HouseErrorCode.WORKPLACE_ADDRESS_NOT_SET);

        verifyNoInteractions(tmapClient);
    }

    @Test
    @DisplayName("location 지오코딩 결과가 없으면 통근시간을 null로 반환한다")
    void returnsUnavailableWhenLocationGeocodingReturnsNoCandidates() {
        givenWorkplace();
        when(tmapClient.geocode(LOCATION)).thenReturn(List.of());

        HouseCommuteEstimateResponse response = houseCommuteService.estimate(USER_ID, LOCATION);

        assertThat(response.getCommuteMinMinutes()).isNull();
        assertThat(response.getCommuteMaxMinutes()).isNull();
    }

    @Test
    @DisplayName("입력한 시·구·동과 다른 후보는 제외하고 일치하는 후보만 계산한다")
    void filtersCandidatesOutsideRequestedRegion() {
        givenWorkplace();
        TmapGeocodeCandidate matching =
                new TmapGeocodeCandidate(37.5, 127.1, "서울", "송파구", "방이동", "방이동");
        TmapGeocodeCandidate other =
                new TmapGeocodeCandidate(37.6, 127.2, "서울", "강동구", "천호동", "천호동");
        when(tmapClient.geocode(LOCATION)).thenReturn(List.of(matching, other));
        when(tmapClient.geocode(WORKPLACE_ROAD_ADDRESS)).thenReturn(List.of(WORKPLACE_CANDIDATE));
        when(tmapClient.getTransitMinutes(
                matching.getLat(), matching.getLon(),
                WORKPLACE_CANDIDATE.getLat(), WORKPLACE_CANDIDATE.getLon()))
                .thenReturn(Optional.of(43));

        HouseCommuteEstimateResponse response = houseCommuteService.estimate(USER_ID, LOCATION);

        assertThat(response.getCommuteMinMinutes()).isEqualTo(43);
        verify(tmapClient, times(1)).getTransitMinutes(anyDouble(), anyDouble(), anyDouble(), anyDouble());
    }

    @Test
    @DisplayName("건물번호가 있는 도로명주소는 동 이름 없이도 계산한다")
    void estimatesDetailedRoadAddressWithoutDong() {
        String roadAddress = "서울시 송파구 송이로15길 5";
        givenWorkplace();
        TmapGeocodeCandidate origin =
                new TmapGeocodeCandidate(37.5, 127.1, "서울", "송파구", "송파동", null);
        when(tmapClient.geocode(roadAddress)).thenReturn(List.of(origin));
        when(tmapClient.geocode(WORKPLACE_ROAD_ADDRESS)).thenReturn(List.of(WORKPLACE_CANDIDATE));
        when(tmapClient.getTransitMinutes(
                origin.getLat(), origin.getLon(),
                WORKPLACE_CANDIDATE.getLat(), WORKPLACE_CANDIDATE.getLon()))
                .thenReturn(Optional.of(38));

        HouseCommuteEstimateResponse response = houseCommuteService.estimate(USER_ID, roadAddress);

        assertThat(response.getCommuteMinMinutes()).isEqualTo(38);
        assertThat(response.getCommuteMaxMinutes()).isEqualTo(38);
    }

    @Test
    @DisplayName("도로명주소 뒤의 동 표기는 지오코딩 검색어에서 제거한다")
    void removesParenthesizedDongFromRoadAddressQuery() {
        String location = "서울시 송파구 송이로15길 5 (송파2동)";
        String roadAddress = "서울시 송파구 송이로15길 5";
        givenWorkplace();
        TmapGeocodeCandidate origin =
                new TmapGeocodeCandidate(37.5, 127.1, "서울", "송파구", "송파동", null);
        when(tmapClient.geocode(roadAddress)).thenReturn(List.of(origin));
        when(tmapClient.geocode(WORKPLACE_ROAD_ADDRESS)).thenReturn(List.of(WORKPLACE_CANDIDATE));
        when(tmapClient.getTransitMinutes(
                origin.getLat(), origin.getLon(),
                WORKPLACE_CANDIDATE.getLat(), WORKPLACE_CANDIDATE.getLon()))
                .thenReturn(Optional.of(38));

        HouseCommuteEstimateResponse response = houseCommuteService.estimate(USER_ID, location);

        assertThat(response.getLocation()).isEqualTo(location);
        assertThat(response.getCommuteMinMinutes()).isEqualTo(38);
        verify(tmapClient).geocode(roadAddress);
    }

    @Test
    @DisplayName("동 이름만 있고 검색 후보의 시·구가 서로 다르면 통근시간을 null로 반환한다")
    void returnsUnavailableWhenDongOnlyLocationSpansRegions() {
        givenWorkplace();
        when(tmapClient.geocode("중앙동")).thenReturn(List.of(
                new TmapGeocodeCandidate(37.4, 127.1, "경기", "성남시", "중앙동", "중앙동"),
                new TmapGeocodeCandidate(35.1, 129.0, "부산", "중구", "중앙동", "중앙동")));

        HouseCommuteEstimateResponse response = houseCommuteService.estimate(USER_ID, "중앙동");

        assertThat(response.getCommuteMinMinutes()).isNull();
        assertThat(response.getCommuteMaxMinutes()).isNull();
        verify(tmapClient, never()).getTransitMinutes(anyDouble(), anyDouble(), anyDouble(), anyDouble());
    }

    @Test
    @DisplayName("직장 주소 지오코딩 결과가 없으면 COMMUTE_LOCATION_NOT_FOUND을 던진다")
    void throwsWhenWorkplaceGeocodingReturnsNoCandidates() {
        givenWorkplace();
        when(tmapClient.geocode(LOCATION)).thenReturn(List.of(
                new TmapGeocodeCandidate(37.509164, 127.124720, "서울", "송파구", "방이동", "방이동")));
        when(tmapClient.geocode(WORKPLACE_ROAD_ADDRESS)).thenReturn(List.of());

        assertThatThrownBy(() -> houseCommuteService.estimate(USER_ID, LOCATION))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(HouseErrorCode.COMMUTE_LOCATION_NOT_FOUND);
    }

    @Test
    @DisplayName("모든 후보의 대중교통 경로가 없으면 COMMUTE_ROUTE_NOT_FOUND을 던진다")
    void throwsWhenNoCandidateHasARoute() {
        givenWorkplace();
        TmapGeocodeCandidate candidate =
                new TmapGeocodeCandidate(37.509164, 127.124720, "서울", "송파구", "방이동", "방이동");
        when(tmapClient.geocode(LOCATION)).thenReturn(List.of(candidate));
        when(tmapClient.geocode(WORKPLACE_ROAD_ADDRESS)).thenReturn(List.of(WORKPLACE_CANDIDATE));
        when(tmapClient.getTransitMinutes(
                candidate.getLat(), candidate.getLon(),
                WORKPLACE_CANDIDATE.getLat(), WORKPLACE_CANDIDATE.getLon()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> houseCommuteService.estimate(USER_ID, LOCATION))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(HouseErrorCode.COMMUTE_ROUTE_NOT_FOUND);
    }

    @Test
    @DisplayName("여러 후보 중 경로가 있는 값들의 최솟값·최댓값을 반환한다")
    void returnsMinAndMaxAcrossCandidatesWithRoutes() {
        givenWorkplace();
        TmapGeocodeCandidate candidate1 =
                new TmapGeocodeCandidate(37.509164, 127.124720, "서울", "송파구", "방이동", "방이동");
        TmapGeocodeCandidate candidate2 =
                new TmapGeocodeCandidate(37.515330, 127.133997, "서울", "송파구", "방이동", "방이동");
        TmapGeocodeCandidate candidate3 =
                new TmapGeocodeCandidate(37.511025, 127.124331, "서울", "송파구", "방이동", "방이동");
        when(tmapClient.geocode(LOCATION)).thenReturn(List.of(candidate1, candidate2, candidate3));
        when(tmapClient.geocode(WORKPLACE_ROAD_ADDRESS)).thenReturn(List.of(WORKPLACE_CANDIDATE));
        when(tmapClient.getTransitMinutes(
                candidate1.getLat(), candidate1.getLon(),
                WORKPLACE_CANDIDATE.getLat(), WORKPLACE_CANDIDATE.getLon()))
                .thenReturn(Optional.of(43));
        when(tmapClient.getTransitMinutes(
                candidate2.getLat(), candidate2.getLon(),
                WORKPLACE_CANDIDATE.getLat(), WORKPLACE_CANDIDATE.getLon()))
                .thenReturn(Optional.of(46));
        when(tmapClient.getTransitMinutes(
                candidate3.getLat(), candidate3.getLon(),
                WORKPLACE_CANDIDATE.getLat(), WORKPLACE_CANDIDATE.getLon()))
                .thenReturn(Optional.empty()); // 경로 없음 — 최소/최대 계산에서 제외되어야 한다.

        HouseCommuteEstimateResponse response = houseCommuteService.estimate(USER_ID, LOCATION);

        assertThat(response.getLocation()).isEqualTo(LOCATION);
        assertThat(response.getCommuteMinMinutes()).isEqualTo(43);
        assertThat(response.getCommuteMaxMinutes()).isEqualTo(46);
    }

    @Test
    @DisplayName("읍·면·동 단위가 없는 위치는 외부 API를 호출하지 않고 통근시간을 null로 반환한다")
    void returnsUnavailableWithoutAdministrativeArea() {
        HouseCommuteEstimateResponse response = houseCommuteService.estimate(USER_ID, "서울 송파구");

        assertThat(response.getCommuteMinMinutes()).isNull();
        assertThat(response.getCommuteMaxMinutes()).isNull();
        verifyNoInteractions(houseCommuteMapper, tmapClient);
    }

    private void givenWorkplace() {
        when(houseCommuteMapper.findWorkplaceAddressByUserId(USER_ID))
                .thenReturn(new WorkplaceAddressResponse(WORKPLACE_ROAD_ADDRESS));
    }
}
