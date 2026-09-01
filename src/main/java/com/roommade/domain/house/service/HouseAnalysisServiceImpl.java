package com.roommade.domain.house.service;

import com.roommade.domain.house.client.HouseImageAnalysisClient;
import com.roommade.domain.house.client.HouseImageExtraction;
import com.roommade.domain.house.code.HouseErrorCode;
import com.roommade.domain.house.dto.response.HouseAnalysisResponse;
import com.roommade.domain.house.dto.response.HouseAnalysisResultResponse;
import com.roommade.domain.house.dto.response.HouseAnalysisStatus;
import com.roommade.global.exception.BusinessException;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class HouseAnalysisServiceImpl implements HouseAnalysisService {

    private static final int MIN_IMAGE_COUNT = 1;
    private static final int MAX_IMAGE_COUNT = 3;
    private static final Set<String> SUPPORTED_CONTENT_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp");

    private final HouseImageAnalysisClient houseImageAnalysisClient;

    @Override
    public HouseAnalysisResponse analyze(List<MultipartFile> images) {
        validateImages(images);

        HouseImageExtraction extraction = houseImageAnalysisClient.analyze(images);
        HouseAnalysisResultResponse result = toResult(extraction);

        return new HouseAnalysisResponse(determineStatus(result), result);
    }

    private void validateImages(List<MultipartFile> images) {
        if (images == null || images.size() < MIN_IMAGE_COUNT || images.size() > MAX_IMAGE_COUNT) {
            throw new BusinessException(HouseErrorCode.INVALID_IMAGE_COUNT);
        }
        for (MultipartFile image : images) {
            if (image.isEmpty()) {
                throw new BusinessException(HouseErrorCode.EMPTY_IMAGE_FILE);
            }
            if (!SUPPORTED_CONTENT_TYPES.contains(image.getContentType())) {
                throw new BusinessException(HouseErrorCode.UNSUPPORTED_IMAGE_FORMAT);
            }
        }
    }

    private HouseAnalysisResultResponse toResult(HouseImageExtraction extraction) {
        return new HouseAnalysisResultResponse(
                extraction.getLocation().orElse(null),
                extraction.getDeposit().orElse(null),
                extraction.getMonthlyRent().orElse(null),
                extraction.getMaintenanceFee().orElse(null),
                extraction.getArea().orElse(null),
                extraction.getStationWalkMinutes().orElse(null),
                extraction.getFloorType().orElse(null),
                extraction.getRoomStructure().orElse(null),
                extraction.getOptionType().orElse(null));
    }

    private HouseAnalysisStatus determineStatus(HouseAnalysisResultResponse result) {
        boolean hasRequiredFields = StringUtils.hasText(result.getLocation())
                && result.getDeposit() != null
                && result.getMonthlyRent() != null;
        return hasRequiredFields ? HouseAnalysisStatus.COMPLETED : HouseAnalysisStatus.PARTIAL;
    }
}
