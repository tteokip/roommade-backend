package com.roommade.domain.house.mapper;

import com.roommade.domain.house.dto.response.PreferenceAnswerResponse;
import com.roommade.domain.house.dto.response.PreferenceQuestionResponse;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface HouseBalanceGameMapper {

    List<PreferenceQuestionResponse> findQuestionsOrderByQuestionOrder();

    List<PreferenceAnswerResponse> findAnswersByComparisonId(@Param("comparisonId") Long comparisonId);

    void insertOrUpdateAnswer(
            @Param("comparisonId") Long comparisonId,
            @Param("questionId") Long questionId,
            @Param("selectedSide") String selectedSide);
}
