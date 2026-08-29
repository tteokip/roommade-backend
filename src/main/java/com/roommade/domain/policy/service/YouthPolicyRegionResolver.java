package com.roommade.domain.policy.service;

import com.roommade.domain.policy.domain.YouthPolicyRegion;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class YouthPolicyRegionResolver {
    private static final Map<String, String> REGION_NAMES = createRegionNames();
    private static final Map<String, String> REGION_CODE_ALIASES = Map.of("51", "42", "52", "45");

    private YouthPolicyRegionResolver() {
    }

    static List<YouthPolicyRegion> resolve(String zipCd) {
        if (zipCd == null || zipCd.isBlank()) {
            return List.of();
        }
        Set<String> regionCodes = new LinkedHashSet<>();
        for (String value : zipCd.split(",")) {
            String normalizedValue = value.trim();
            if ("00000".equals(normalizedValue) || "전국".equals(normalizedValue)) {
                return List.of(new YouthPolicyRegion("00", "전국", true));
            }
            if (normalizedValue.matches("\\d{5}")) {
                String regionCode = REGION_CODE_ALIASES.getOrDefault(normalizedValue.substring(0, 2), normalizedValue.substring(0, 2));
                if (REGION_NAMES.containsKey(regionCode)) {
                    regionCodes.add(regionCode);
                }
            }
        }
        if (regionCodes.containsAll(REGION_NAMES.keySet())) {
            return List.of(new YouthPolicyRegion("00", "전국", true));
        }
        List<YouthPolicyRegion> regions = new ArrayList<>();
        for (String regionCode : regionCodes) {
            regions.add(new YouthPolicyRegion(regionCode, REGION_NAMES.get(regionCode), false));
        }
        return regions;
    }

    private static Map<String, String> createRegionNames() {
        Map<String, String> regionNames = new LinkedHashMap<>();
        regionNames.put("11", "서울특별시"); regionNames.put("26", "부산광역시");
        regionNames.put("27", "대구광역시"); regionNames.put("28", "인천광역시");
        regionNames.put("29", "광주광역시"); regionNames.put("30", "대전광역시");
        regionNames.put("31", "울산광역시"); regionNames.put("36", "세종특별자치시");
        regionNames.put("41", "경기도"); regionNames.put("42", "강원특별자치도");
        regionNames.put("43", "충청북도"); regionNames.put("44", "충청남도");
        regionNames.put("45", "전북특별자치도"); regionNames.put("46", "전라남도");
        regionNames.put("47", "경상북도"); regionNames.put("48", "경상남도");
        regionNames.put("50", "제주특별자치도");
        return Map.copyOf(regionNames);
    }
}
