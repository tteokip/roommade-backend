package com.roommade.domain.house.service;

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
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class HouseCommuteServiceImpl implements HouseCommuteService {

    private static final Pattern ADMINISTRATIVE_AREA_PATTERN =
            Pattern.compile("[가-힣0-9·]+(?:읍|면|동|가|리)(?:\\s|$)");
    private static final Pattern DISTRICT_PATTERN = Pattern.compile("[가-힣0-9·]+(?:구|군)(?:\\s|$)");
    private static final Pattern CITY_PATTERN =
            Pattern.compile("^[가-힣]+(?:특별시|광역시|특별자치시|특별자치도|도|시)(?:\\s|$)");
    private static final Pattern ROAD_ADDRESS_PATTERN =
            Pattern.compile("[가-힣0-9·.-]+(?:로|길)\\s*\\d+(?:-\\d+)?");
    private static final Pattern PARENTHESIZED_ADMINISTRATIVE_AREA_PATTERN =
            Pattern.compile("\\s*\\([^)]*(?:읍|면|동|가|리)\\)\\s*$");

    private final HouseCommuteMapper houseCommuteMapper;
    private final TmapClient tmapClient;

    @Override
    public HouseCommuteEstimateResponse estimate(Long userId, String location) {
        if (!StringUtils.hasText(location)) {
            throw new BusinessException(CommonErrorCode.INVALID_REQUEST);
        }
        String normalizedLocation = location.trim().replaceAll("\\s+", " ");
        boolean detailedRoadAddress = ROAD_ADDRESS_PATTERN.matcher(normalizedLocation).find();
        // 지역명은 읍·면·동 단위가 필요하지만, 건물번호가 있는 도로명주소는 동 없이도 계산할 수 있다.
        if (!detailedRoadAddress && !ADMINISTRATIVE_AREA_PATTERN.matcher(normalizedLocation).find()) {
            return unavailable(normalizedLocation);
        }

        String workplaceRoadAddress = resolveWorkplaceRoadAddress(userId);
        String geocodeLocation = detailedRoadAddress
                ? PARENTHESIZED_ADMINISTRATIVE_AREA_PATTERN.matcher(normalizedLocation).replaceFirst("")
                : normalizedLocation;

        List<TmapGeocodeCandidate> originCandidates = tmapClient.geocode(geocodeLocation).stream()
                .filter(candidate -> detailedRoadAddress
                        ? matchesRoadAddressRegion(normalizedLocation, candidate)
                        : matchesOriginRegion(normalizedLocation, candidate))
                .collect(Collectors.toList());
        boolean districtSpecified = DISTRICT_PATTERN.matcher(normalizedLocation).find();
        // 구·군이 없는 동 이름은 전국에 중복될 수 있다. 서로 다른 지역의 후보가 섞이면 임의로 고르지 않는다.
        if (originCandidates.isEmpty() || (!districtSpecified && hasMixedRegions(originCandidates))) {
            return unavailable(normalizedLocation);
        }

        TmapGeocodeCandidate workplaceCandidate = resolveWorkplaceCandidate(workplaceRoadAddress);

        List<Integer> transitMinutesCandidates = originCandidates.stream()
                .map(candidate -> tmapClient.getTransitMinutes(
                        candidate.getLat(), candidate.getLon(),
                        workplaceCandidate.getLat(), workplaceCandidate.getLon()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        if (transitMinutesCandidates.isEmpty()) {
            throw new BusinessException(HouseErrorCode.COMMUTE_ROUTE_NOT_FOUND);
        }

        // 검색된 출발 후보별 시간의 최소·최대를 예상 범위로 반환한다.
        int minMinutes = transitMinutesCandidates.stream().min(Integer::compareTo).orElseThrow();
        int maxMinutes = transitMinutesCandidates.stream().max(Integer::compareTo).orElseThrow();
        return new HouseCommuteEstimateResponse(normalizedLocation, minMinutes, maxMinutes);
    }

    private String resolveWorkplaceRoadAddress(Long userId) {
        WorkplaceAddressResponse workplace = houseCommuteMapper.findWorkplaceAddressByUserId(userId);
        if (workplace == null || !StringUtils.hasText(workplace.getWorkplaceRoadAddress())) {
            throw new BusinessException(HouseErrorCode.WORKPLACE_ADDRESS_NOT_SET);
        }
        return workplace.getWorkplaceRoadAddress();
    }

    private TmapGeocodeCandidate resolveWorkplaceCandidate(String workplaceRoadAddress) {
        List<TmapGeocodeCandidate> workplaceCandidates = tmapClient.geocode(workplaceRoadAddress);
        if (workplaceCandidates.isEmpty()) {
            throw new BusinessException(HouseErrorCode.COMMUTE_LOCATION_NOT_FOUND);
        }
        // TMAP 응답 순서를 신뢰하지 않고 입력한 직장 주소와 시·구가 일치하는 후보만 사용한다.
        return workplaceCandidates.stream()
                .filter(candidate -> matchesWorkplaceRegion(workplaceRoadAddress, candidate))
                .findFirst()
                .orElseThrow(() -> new BusinessException(HouseErrorCode.COMMUTE_LOCATION_NOT_FOUND));
    }

    private boolean matchesOriginRegion(String location, TmapGeocodeCandidate candidate) {
        boolean administrativeAreaMatches = contains(location, candidate.getLegalDong())
                || contains(location, candidate.getAdminDong());
        if (!administrativeAreaMatches) {
            return false;
        }
        if (DISTRICT_PATTERN.matcher(location).find()) {
            return contains(location, candidate.getGuGun());
        }
        return true;
    }

    private boolean matchesRoadAddressRegion(String address, TmapGeocodeCandidate candidate) {
        if (DISTRICT_PATTERN.matcher(address).find() && !contains(address, candidate.getGuGun())) {
            return false;
        }
        return !CITY_PATTERN.matcher(address).find() || cityMatches(address, candidate.getCityDo());
    }

    private boolean matchesWorkplaceRegion(String address, TmapGeocodeCandidate candidate) {
        return contains(address, candidate.getGuGun()) && cityMatches(address, candidate.getCityDo());
    }

    private boolean hasMixedRegions(List<TmapGeocodeCandidate> candidates) {
        return candidates.stream()
                .map(candidate -> candidate.getCityDo() + "|" + candidate.getGuGun())
                .distinct()
                .count() > 1;
    }

    private boolean cityMatches(String address, String cityDo) {
        if (!StringUtils.hasText(cityDo)) {
            return false;
        }
        String cityStem = cityDo
                .replace("특별자치도", "")
                .replace("특별자치시", "")
                .replace("특별시", "")
                .replace("광역시", "")
                .replaceAll("[도시]$", "");
        return StringUtils.hasText(cityStem) && address.contains(cityStem);
    }

    private boolean contains(String source, String value) {
        return StringUtils.hasText(value) && source.contains(value);
    }

    private HouseCommuteEstimateResponse unavailable(String location) {
        return new HouseCommuteEstimateResponse(location, null, null);
    }
}
