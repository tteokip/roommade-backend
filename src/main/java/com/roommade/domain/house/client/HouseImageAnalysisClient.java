package com.roommade.domain.house.client;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface HouseImageAnalysisClient {

    HouseImageExtraction analyze(List<MultipartFile> images);
}
