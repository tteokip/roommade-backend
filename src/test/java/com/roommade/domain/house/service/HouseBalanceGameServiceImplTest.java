package com.roommade.domain.house.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.roommade.domain.house.code.HouseErrorCode;
import com.roommade.domain.house.dto.response.BalanceGameProgressResponse;
import com.roommade.domain.house.dto.response.BalanceGameQuestionResponse;
import com.roommade.domain.house.dto.response.BalanceGameQuestionsResponse;
import com.roommade.domain.house.dto.response.BalanceGameResultResponse;
import com.roommade.domain.house.dto.response.ComparisonFactor;
import com.roommade.domain.house.dto.response.HouseComparisonCurrentResponse;
import com.roommade.domain.house.dto.response.HouseResponse;
import com.roommade.domain.house.dto.response.PreferenceAnswerResponse;
import com.roommade.domain.house.dto.response.PreferenceQuestionResponse;
import com.roommade.domain.house.mapper.HouseBalanceGameMapper;
import com.roommade.domain.house.mapper.HouseComparisonMapper;
import com.roommade.global.exception.BusinessException;
import com.roommade.global.exception.CommonErrorCode;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HouseBalanceGameServiceImplTest {

    private static final Long USER_ID = 1L;
    private static final Long COMPARISON_ID = 100L;

    @Mock
    private HouseComparisonMapper houseComparisonMapper;

    @Mock
    private HouseBalanceGameMapper houseBalanceGameMapper;

    @InjectMocks
    private HouseBalanceGameServiceImpl houseBalanceGameService;

    @Test
    @DisplayName("commuteMinutes가 없으면 Q1/Q5를 제외하고 다음 순서의 대체 질문으로 채운다")
    void excludesQuestionsRequiringMissingCommuteAndFillsWithSubstitutes() {
        HouseResponse houseA = house(
                120_000_000L, 500_000L, 50_000L, new BigDecimal("30.00"), 10, null, "부분옵션");
        HouseResponse houseB = house(
                100_000_000L, 600_000L, 50_000L, new BigDecimal("25.00"), 5, 30, "풀옵션");
        givenComparison(houseA, houseB);
        givenAnswers();

        BalanceGameQuestionsResponse response = houseBalanceGameService.getQuestions(USER_ID);

        assertThat(questionIds(response)).containsExactly(2L, 3L, 4L, 6L, 7L);
    }

    @Test
    @DisplayName("DB 순서가 빠르더라도 두 요소가 같은 집에 유리한 질문은 후순위로 둔다")
    void prioritizesQuestionsFavoringDifferentHouses() {
        HouseResponse houseA = house(
                120_000_000L, 500_000L, 50_000L, new BigDecimal("30.00"), 10, 10, "부분옵션");
        HouseResponse houseB = house(
                100_000_000L, 600_000L, 50_000L, new BigDecimal("25.00"), 5, 20, "풀옵션");
        givenComparison(houseA, houseB);
        givenAnswers();

        BalanceGameQuestionsResponse response = houseBalanceGameService.getQuestions(USER_ID);

        assertThat(questionIds(response)).containsExactly(2L, 3L, 4L, 6L, 7L);
    }

    @Test
    @DisplayName("maintenanceFee가 null이면 0으로 취급하지 않아 MONTHLY_COST가 비교 불가로 제외된다")
    void doesNotTreatNullMaintenanceFeeAsZero() {
        HouseResponse houseA = house(
                100_000_000L, 500_000L, null, new BigDecimal("30.00"), 8, 30, "풀옵션");
        HouseResponse houseB = house(
                90_000_000L, 550_000L, 0L, new BigDecimal("28.00"), 5, 20, "옵션 없음");
        givenComparison(houseA, houseB);
        givenAnswers();

        BalanceGameQuestionsResponse response = houseBalanceGameService.getQuestions(USER_ID);

        assertThat(questionIds(response)).containsExactly(2L, 5L, 7L, 8L, 10L);
    }

    @Test
    @DisplayName("optionType이 풀옵션/부분옵션/옵션 없음 중 하나가 아니면 OPTION이 비교 불가로 제외된다")
    void optionTypeOutsideKnownGradesIsNotComparable() {
        HouseResponse houseA = house(
                100_000_000L, 500_000L, 50_000L, new BigDecimal("30.00"), 8, 30, "냉장고");
        HouseResponse houseB = house(
                90_000_000L, 550_000L, 20_000L, new BigDecimal("28.00"), 5, 20, "풀옵션");
        givenComparison(houseA, houseB);
        givenAnswers();

        BalanceGameQuestionsResponse response = houseBalanceGameService.getQuestions(USER_ID);

        assertThat(questionIds(response)).containsExactly(1L, 2L, 4L, 5L, 6L);
    }

    @Test
    @DisplayName("MONTHLY_COST는 monthlyRent와 maintenanceFee의 합으로 비교한다")
    void calculatesMonthlyCostAsRentPlusMaintenanceFee() {
        // A: 400,000 + 200,000 = 600,000 / B: 500,000 + 50,000 = 550,000 → B가 더 저렴하다.
        // monthlyRent만 비교하면(400,000 < 500,000) A가 더 저렴한 것으로 잘못 판정된다.
        HouseResponse houseA = house(
                80_000_000L, 400_000L, 200_000L, new BigDecimal("25.00"), 5, 20, "풀옵션");
        HouseResponse houseB = house(
                90_000_000L, 500_000L, 50_000L, new BigDecimal("25.00"), 5, 20, "풀옵션");
        givenComparison(houseA, houseB);
        givenAnswers(new PreferenceAnswerResponse(4L, "A"));

        BalanceGameResultResponse result = houseBalanceGameService.getResult(USER_ID);

        assertThat(result.getSelectedFactors()).containsEntry(ComparisonFactor.MONTHLY_COST, 1);
        assertThat(result.getHouseAScore()).isZero();
        assertThat(result.getHouseBScore()).isEqualTo(1);
        assertThat(result.getResult()).isEqualTo("B");
    }

    @Test
    @DisplayName("OPTION은 풀옵션 > 부분옵션 > 옵션 없음 순으로 비교한다")
    void comparesOptionGradeOrdering() {
        HouseResponse houseA = house(
                100_000_000L, 500_000L, 50_000L, new BigDecimal("25.00"), 5, 20, "부분옵션");
        HouseResponse houseB = house(
                100_000_000L, 500_000L, 50_000L, new BigDecimal("25.00"), 10, 20, "풀옵션");
        givenComparison(houseA, houseB);
        givenAnswers(new PreferenceAnswerResponse(8L, "A"));

        BalanceGameResultResponse result = houseBalanceGameService.getResult(USER_ID);

        assertThat(result.getSelectedFactors()).containsEntry(ComparisonFactor.OPTION, 1);
        assertThat(result.getHouseBScore()).isEqualTo(1);
        assertThat(result.getResult()).isEqualTo("B");
    }

    @Test
    @DisplayName("같은 factor를 여러 질문에서 선택하면 가중치가 누적된다")
    void accumulatesWeightWhenSameFactorIsChosenMultipleTimes() {
        HouseResponse houseA = allComparableHouseA();
        HouseResponse houseB = allComparableHouseB();
        givenComparison(houseA, houseB);
        givenAnswers(
                new PreferenceAnswerResponse(1L, "A"), // MONTHLY_COST
                new PreferenceAnswerResponse(2L, "A"), // STATION
                new PreferenceAnswerResponse(3L, "A"), // OPTION
                new PreferenceAnswerResponse(4L, "A"), // MONTHLY_COST
                new PreferenceAnswerResponse(5L, "B")); // COMMUTE

        BalanceGameResultResponse result = houseBalanceGameService.getResult(USER_ID);

        assertThat(result.getSelectedFactors())
                .containsEntry(ComparisonFactor.MONTHLY_COST, 2)
                .containsEntry(ComparisonFactor.STATION, 1)
                .containsEntry(ComparisonFactor.OPTION, 1)
                .containsEntry(ComparisonFactor.COMMUTE, 1)
                .doesNotContainKey(ComparisonFactor.DEPOSIT)
                .doesNotContainKey(ComparisonFactor.AREA);
    }

    @Test
    @DisplayName("A 매물의 점수가 더 높으면 result는 A다")
    void houseAWinsWhenScoreIsHigher() {
        HouseResponse houseA = monthlyCostDepositAndAreaHouseA();
        HouseResponse houseB = monthlyCostDepositAndAreaHouseB();
        givenComparison(houseA, houseB);
        givenAnswers(new PreferenceAnswerResponse(4L, "A"), new PreferenceAnswerResponse(7L, "B"));

        BalanceGameResultResponse result = houseBalanceGameService.getResult(USER_ID);

        assertThat(result.getHouseAScore()).isEqualTo(2);
        assertThat(result.getHouseBScore()).isZero();
        assertThat(result.getResult()).isEqualTo("A");
    }

    @Test
    @DisplayName("B 매물의 점수가 더 높으면 result는 B다")
    void houseBWinsWhenScoreIsHigher() {
        HouseResponse houseA = monthlyCostDepositAndAreaHouseA();
        HouseResponse houseB = monthlyCostDepositAndAreaHouseB();
        givenComparison(houseA, houseB);
        givenAnswers(new PreferenceAnswerResponse(4L, "B"), new PreferenceAnswerResponse(7L, "A"));

        BalanceGameResultResponse result = houseBalanceGameService.getResult(USER_ID);

        assertThat(result.getHouseAScore()).isZero();
        assertThat(result.getHouseBScore()).isEqualTo(2);
        assertThat(result.getResult()).isEqualTo("B");
    }

    @Test
    @DisplayName("점수가 같으면 result는 TIE다")
    void resultIsTieWhenScoresAreEqual() {
        HouseResponse houseA = monthlyCostDepositAndAreaHouseA();
        HouseResponse houseB = monthlyCostDepositAndAreaHouseB();
        givenComparison(houseA, houseB);
        givenAnswers(new PreferenceAnswerResponse(4L, "A"), new PreferenceAnswerResponse(7L, "A"));

        BalanceGameResultResponse result = houseBalanceGameService.getResult(USER_ID);

        assertThat(result.getHouseAScore()).isEqualTo(1);
        assertThat(result.getHouseBScore()).isEqualTo(1);
        assertThat(result.getResult()).isEqualTo("TIE");
        assertThat(result.getMatchedFactors()).isEmpty();
    }

    @Test
    @DisplayName("비교할 수 없는 요소는 가중치 선택 여부와 관계없이 excludedFactors에 포함한다")
    void returnsAllUnavailableFactorsAsExcluded() {
        HouseResponse houseA = house(
                120_000_000L, 500_000L, 50_000L, new BigDecimal("25.00"), 5, null, "풀옵션");
        HouseResponse houseB = house(
                100_000_000L, 600_000L, 0L, new BigDecimal("25.00"), 5, null, "풀옵션");
        givenComparison(houseA, houseB);
        givenAnswers(new PreferenceAnswerResponse(4L, "A"));

        BalanceGameResultResponse result = houseBalanceGameService.getResult(USER_ID);

        assertThat(result.getExcludedFactors())
                .containsExactly(
                        ComparisonFactor.COMMUTE,
                        ComparisonFactor.STATION,
                        ComparisonFactor.AREA,
                        ComparisonFactor.OPTION);
    }

    @Test
    @DisplayName("출제된 질문에 모두 답하지 않으면 결과 조회는 실패한다")
    void throwsWhenNotAllServedQuestionsAreAnswered() {
        HouseResponse houseA = allComparableHouseA();
        HouseResponse houseB = allComparableHouseB();
        givenComparison(houseA, houseB);
        givenAnswers(new PreferenceAnswerResponse(1L, "A"));

        assertThatThrownBy(() -> houseBalanceGameService.getResult(USER_ID))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(HouseErrorCode.BALANCE_GAME_INCOMPLETE);
    }

    @Test
    @DisplayName("A 또는 B 매물이 등록되어 있지 않으면 HOUSE_PAIR_NOT_READY를 던진다")
    void throwsWhenHousePairIsNotReady() {
        when(houseComparisonMapper.findCurrentByUserId(USER_ID))
                .thenReturn(new HouseComparisonCurrentResponse(COMPARISON_ID, allComparableHouseA(), null, false));

        assertThatThrownBy(() -> houseBalanceGameService.getQuestions(USER_ID))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(HouseErrorCode.HOUSE_PAIR_NOT_READY);

        verify(houseBalanceGameMapper, never()).findQuestionsOrderByQuestionOrder();
    }

    @Test
    @DisplayName("모든 factor가 비교 불가하면 출제 가능한 질문이 없어 실패한다")
    void throwsWhenNoQuestionIsAvailable() {
        HouseResponse houseA = house(
                100_000_000L, 500_000L, 50_000L, new BigDecimal("25.00"), 5, 20, "풀옵션");
        HouseResponse houseB = house(
                100_000_000L, 500_000L, 50_000L, new BigDecimal("25.00"), 5, 20, "풀옵션");
        givenComparison(houseA, houseB);

        assertThatThrownBy(() -> houseBalanceGameService.getQuestions(USER_ID))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(HouseErrorCode.BALANCE_GAME_NOT_AVAILABLE);
    }

    @Test
    @DisplayName("모든 요소에서 같은 집이 우세하면 출제 가능한 질문이 없어 실패한다")
    void throwsWhenAllFactorsFavorSameHouse() {
        HouseResponse houseA = house(
                80_000_000L, 400_000L, 30_000L, new BigDecimal("30.00"), 3, 15, "풀옵션");
        HouseResponse houseB = house(
                100_000_000L, 500_000L, 50_000L, new BigDecimal("25.00"), 8, 30, "부분옵션");
        givenComparison(houseA, houseB);

        assertThatThrownBy(() -> houseBalanceGameService.getQuestions(USER_ID))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(HouseErrorCode.BALANCE_GAME_NOT_AVAILABLE);
    }

    @Test
    @DisplayName("현재 출제 대상이 아닌 질문에 답하면 실패하고 upsert를 호출하지 않는다")
    void throwsWhenAnsweringQuestionNotServed() {
        HouseResponse houseA = allComparableHouseA();
        HouseResponse houseB = allComparableHouseB();
        givenComparison(houseA, houseB);

        assertThatThrownBy(() -> houseBalanceGameService.submitAnswer(USER_ID, 999L, "A"))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(HouseErrorCode.BALANCE_GAME_QUESTION_NOT_SERVED);

        verify(houseBalanceGameMapper, never()).insertOrUpdateAnswer(any(), any(), any());
    }

    @Test
    @DisplayName("selectedSide가 A/B가 아니면 조회나 저장 없이 실패한다")
    void throwsWhenSelectedSideIsInvalid() {
        assertThatThrownBy(() -> houseBalanceGameService.submitAnswer(USER_ID, 1L, "C"))
                .isInstanceOf(BusinessException.class)
                .extracting(ex -> ((BusinessException) ex).getErrorCode())
                .isEqualTo(CommonErrorCode.INVALID_INPUT_VALUE);

        verifyNoInteractions(houseComparisonMapper, houseBalanceGameMapper);
    }

    @Test
    @DisplayName("답변을 upsert하고 갱신된 진행률을 반환한다")
    void submitsAnswerAndReturnsUpdatedProgress() {
        HouseResponse houseA = allComparableHouseA();
        HouseResponse houseB = allComparableHouseB();
        when(houseComparisonMapper.findCurrentByUserId(USER_ID))
                .thenReturn(new HouseComparisonCurrentResponse(COMPARISON_ID, houseA, houseB, true));
        when(houseBalanceGameMapper.findQuestionsOrderByQuestionOrder()).thenReturn(allQuestions());
        when(houseBalanceGameMapper.findAnswersByComparisonId(COMPARISON_ID))
                .thenReturn(List.of(new PreferenceAnswerResponse(1L, "A")));

        BalanceGameProgressResponse response = houseBalanceGameService.submitAnswer(USER_ID, 1L, "A");

        verify(houseBalanceGameMapper).insertOrUpdateAnswer(COMPARISON_ID, 1L, "A");
        assertThat(response.getTotalQuestions()).isEqualTo(5);
        assertThat(response.getAnsweredQuestions()).isEqualTo(1);
        assertThat(response.isCompleted()).isFalse();
    }

    @Test
    @DisplayName("일부만 답변하면 completed는 false이고 answeredQuestions는 답변 수와 같다")
    void reportsPartialProgress() {
        HouseResponse houseA = allComparableHouseA();
        HouseResponse houseB = allComparableHouseB();
        givenComparison(houseA, houseB);
        givenAnswers(new PreferenceAnswerResponse(1L, "A"), new PreferenceAnswerResponse(2L, "B"));

        BalanceGameQuestionsResponse response = houseBalanceGameService.getQuestions(USER_ID);

        assertThat(response.getTotalQuestions()).isEqualTo(5);
        assertThat(response.getAnsweredQuestions()).isEqualTo(2);
        assertThat(response.isCompleted()).isFalse();
    }

    private void givenComparison(HouseResponse houseA, HouseResponse houseB) {
        when(houseComparisonMapper.findCurrentByUserId(USER_ID))
                .thenReturn(new HouseComparisonCurrentResponse(COMPARISON_ID, houseA, houseB, true));
        when(houseBalanceGameMapper.findQuestionsOrderByQuestionOrder()).thenReturn(allQuestions());
    }

    private void givenAnswers(PreferenceAnswerResponse... answers) {
        when(houseBalanceGameMapper.findAnswersByComparisonId(COMPARISON_ID)).thenReturn(List.of(answers));
    }

    private List<Long> questionIds(BalanceGameQuestionsResponse response) {
        return response.getQuestions().stream()
                .map(BalanceGameQuestionResponse::getQuestionId)
                .collect(Collectors.toList());
    }

    /** 6개 비교 요소가 모두 비교 가능해 기본 질문 Q1~Q5가 그대로 출제되는 매물 조합. */
    private HouseResponse allComparableHouseA() {
        return house(120_000_000L, 500_000L, 50_000L, new BigDecimal("30.00"), 10, 30, "부분옵션");
    }

    private HouseResponse allComparableHouseB() {
        return house(100_000_000L, 650_000L, 0L, new BigDecimal("25.00"), 5, 20, "풀옵션");
    }

    /** MONTHLY_COST/DEPOSIT/AREA만 비교 가능해 Q4와 Q7만 출제되는 매물 조합. */
    private HouseResponse monthlyCostDepositAndAreaHouseA() {
        return house(120_000_000L, 500_000L, 50_000L, new BigDecimal("30.00"), 5, 20, "풀옵션");
    }

    private HouseResponse monthlyCostDepositAndAreaHouseB() {
        return house(100_000_000L, 600_000L, 0L, new BigDecimal("25.00"), 5, 20, "풀옵션");
    }

    private HouseResponse house(
            Long deposit, Long monthlyRent, Long maintenanceFee, BigDecimal area,
            Integer stationWalkMinutes, Integer commuteMinutes, String optionType) {
        return new HouseResponse(1L, "위치", deposit, monthlyRent, maintenanceFee, area,
                stationWalkMinutes, commuteMinutes, "고층", "원룸", optionType);
    }

    private List<PreferenceQuestionResponse> allQuestions() {
        return List.of(
                new PreferenceQuestionResponse(1L, "월세·관리비 부담이 적은 집", ComparisonFactor.MONTHLY_COST,
                        "직장까지 가까운 집", ComparisonFactor.COMMUTE),
                new PreferenceQuestionResponse(2L, "역과 가까운 집", ComparisonFactor.STATION,
                        "더 넓은 집", ComparisonFactor.AREA),
                new PreferenceQuestionResponse(3L, "옵션이 좋은 집", ComparisonFactor.OPTION,
                        "월세·관리비 부담이 적은 집", ComparisonFactor.MONTHLY_COST),
                new PreferenceQuestionResponse(4L, "월세·관리비 부담이 적은 집", ComparisonFactor.MONTHLY_COST,
                        "보증금이 적은 집", ComparisonFactor.DEPOSIT),
                new PreferenceQuestionResponse(5L, "더 넓은 집", ComparisonFactor.AREA,
                        "직장까지 가까운 집", ComparisonFactor.COMMUTE),
                new PreferenceQuestionResponse(6L, "월세·관리비 부담이 적은 집", ComparisonFactor.MONTHLY_COST,
                        "역과 가까운 집", ComparisonFactor.STATION),
                new PreferenceQuestionResponse(7L, "보증금이 적은 집", ComparisonFactor.DEPOSIT,
                        "더 넓은 집", ComparisonFactor.AREA),
                new PreferenceQuestionResponse(8L, "옵션이 좋은 집", ComparisonFactor.OPTION,
                        "역과 가까운 집", ComparisonFactor.STATION),
                new PreferenceQuestionResponse(9L, "보증금이 적은 집", ComparisonFactor.DEPOSIT,
                        "직장까지 가까운 집", ComparisonFactor.COMMUTE),
                new PreferenceQuestionResponse(10L, "보증금이 적은 집", ComparisonFactor.DEPOSIT,
                        "옵션이 좋은 집", ComparisonFactor.OPTION));
    }
}
