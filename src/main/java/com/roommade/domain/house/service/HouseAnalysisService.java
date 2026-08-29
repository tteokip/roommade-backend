package com.roommade.domain.house.service;

import com.roommade.domain.house.dto.response.HouseAnalysisResponse;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface HouseAnalysisService {

    HouseAnalysisResponse analyze(List<MultipartFile> images);
}
