package com.roommade.domain.house.service;

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
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HouseBalanceGameServiceImpl implements HouseBalanceGameService {

    private static final int MAX_QUESTIONS = 5;
    private static final int COMMUTE_ADVANTAGE_MINUTES = 5;
    private static final String SELECTED_SIDE_A = "A";
    private static final String SELECTED_SIDE_B = "B";
    private static final Map<String, Integer> OPTION_RANKS =
            Map.of("옵션 없음", 1, "부분옵션", 2, "풀옵션", 3);

    private final HouseComparisonMapper houseComparisonMapper;
    private final HouseBalanceGameMapper houseBalanceGameMapper;

    @Override
    public BalanceGameQuestionsResponse getQuestions(Long userId) {
        HouseComparisonCurrentResponse comparison = resolveReadyComparison(userId);
        List<PreferenceQuestionResponse> served = resolveServedQuestions(comparison);
        Map<Long, String> answers = answersByQuestionId(comparison.getComparisonId());
        return toQuestionsResponse(served, answers);
    }

    @Override
    @Transactional
    public BalanceGameProgressResponse submitAnswer(Long userId, Long questionId, String selectedSide) {
        if (!SELECTED_SIDE_A.equals(selectedSide) && !SELECTED_SIDE_B.equals(selectedSide)) {
            throw new BusinessException(CommonErrorCode.INVALID_INPUT_VALUE);
        }

        HouseComparisonCurrentResponse comparison = resolveReadyComparison(userId);
        List<PreferenceQuestionResponse> served = resolveServedQuestions(comparison);

        boolean isServed = served.stream().anyMatch(question -> question.getId().equals(questionId));
        if (!isServed) {
            throw new BusinessException(HouseErrorCode.BALANCE_GAME_QUESTION_NOT_SERVED);
        }

        houseBalanceGameMapper.insertOrUpdateAnswer(comparison.getComparisonId(), questionId, selectedSide);

        Map<Long, String> answers = answersByQuestionId(comparison.getComparisonId());
        return toProgressResponse(served, answers);
    }

    @Override
    public BalanceGameResultResponse getResult(Long userId) {
        HouseComparisonCurrentResponse comparison = resolveReadyComparison(userId);
        List<PreferenceQuestionResponse> served = resolveServedQuestions(comparison);
        Map<Long, String> answers = answersByQuestionId(comparison.getComparisonId());

        boolean allAnswered = served.stream().allMatch(question -> answers.containsKey(question.getId()));
        if (!allAnswered) {
            throw new BusinessException(HouseErrorCode.BALANCE_GAME_INCOMPLETE);
        }

        Map<ComparisonFactor, Integer> factorWeights = calculateFactorWeights(served, answers);
        return calculateResult(factorWeights, comparison.getHouseA(), comparison.getHouseB());
    }

    private HouseComparisonCurrentResponse resolveReadyComparison(Long userId) {
        HouseComparisonCurrentResponse comparison = houseComparisonMapper.findCurrentByUserId(userId);
        if (comparison == null || comparison.getHouseA() == null || comparison.getHouseB() == null) {
            throw new BusinessException(HouseErrorCode.HOUSE_PAIR_NOT_READY);
        }
        return comparison;
    }

    /** 두 요소가 서로 다른 집에 유리한 질문을 순서대로 최대 {@value MAX_QUESTIONS}개 고른다. */
    private List<PreferenceQuestionResponse> resolveServedQuestions(HouseComparisonCurrentResponse comparison) {
        HouseResponse houseA = comparison.getHouseA();
        HouseResponse houseB = comparison.getHouseB();
        List<PreferenceQuestionResponse> served = houseBalanceGameMapper.findQuestionsOrderByQuestionOrder().stream()
                .filter(question -> isComparableQuestion(question, houseA, houseB))
                .filter(question -> favorsDifferentHouses(question, houseA, houseB))
                .limit(MAX_QUESTIONS)
                .collect(Collectors.toList());

        if (served.isEmpty()) {
            throw new BusinessException(HouseErrorCode.BALANCE_GAME_NOT_AVAILABLE);
        }
        return served;
    }

    private boolean isComparableQuestion(
            PreferenceQuestionResponse question, HouseResponse houseA, HouseResponse houseB) {
        FactorComparison optionAComparison = compare(question.getOptionAFactor(), houseA, houseB);
        FactorComparison optionBComparison = compare(question.getOptionBFactor(), houseA, houseB);
        return optionAComparison.isComparable() && optionBComparison.isComparable();
    }

    private boolean favorsDifferentHouses(
            PreferenceQuestionResponse question, HouseResponse houseA, HouseResponse houseB) {
        return compare(question.getOptionAFactor(), houseA, houseB).isHouseABetter()
                != compare(question.getOptionBFactor(), houseA, houseB).isHouseABetter();
    }

    private Map<ComparisonFactor, Integer> calculateFactorWeights(
            List<PreferenceQuestionResponse> served, Map<Long, String> answers) {
        Map<ComparisonFactor, Integer> factorWeights = new EnumMap<>(ComparisonFactor.class);
        for (PreferenceQuestionResponse question : served) {
            String selectedSide = answers.get(question.getId());
            ComparisonFactor factor = SELECTED_SIDE_A.equals(selectedSide)
                    ? question.getOptionAFactor()
                    : question.getOptionBFactor();
            factorWeights.merge(factor, 1, Integer::sum);
        }
        return factorWeights;
    }

    private BalanceGameResultResponse calculateResult(
            Map<ComparisonFactor, Integer> factorWeights, HouseResponse houseA, HouseResponse houseB) {
        int houseAScore = 0;
        int houseBScore = 0;
        Map<ComparisonFactor, Integer> houseAWinningFactors = new EnumMap<>(ComparisonFactor.class);
        Map<ComparisonFactor, Integer> houseBWinningFactors = new EnumMap<>(ComparisonFactor.class);
        List<ComparisonFactor> excludedFactors = new ArrayList<>();

        for (ComparisonFactor factor : ComparisonFactor.values()) {
            if (!compare(factor, houseA, houseB).isComparable()) {
                excludedFactors.add(factor);
            }
        }

        for (Map.Entry<ComparisonFactor, Integer> entry : factorWeights.entrySet()) {
            FactorComparison factorComparison = compare(entry.getKey(), houseA, houseB);
            if (factorComparison.isHouseABetter()) {
                houseAScore += entry.getValue();
                houseAWinningFactors.put(entry.getKey(), entry.getValue());
            } else {
                houseBScore += entry.getValue();
                houseBWinningFactors.put(entry.getKey(), entry.getValue());
            }
        }

        String result = houseAScore > houseBScore ? "A" : houseBScore > houseAScore ? "B" : "TIE";
        Map<ComparisonFactor, Integer> matchedFactors = "A".equals(result)
                ? houseAWinningFactors
                : "B".equals(result) ? houseBWinningFactors : new EnumMap<>(ComparisonFactor.class);
        return new BalanceGameResultResponse(
                result,
                houseAScore,
                houseBScore,
                factorWeights,
                matchedFactors,
                excludedFactors);
    }

    private FactorComparison compare(ComparisonFactor factor, HouseResponse houseA, HouseResponse houseB) {
        switch (factor) {
            case DEPOSIT:
                return compareLowerIsBetter(houseA.getDeposit(), houseB.getDeposit());
            case MONTHLY_COST:
                return compareLowerIsBetter(monthlyCost(houseA), monthlyCost(houseB));
            case COMMUTE:
                return compareCommuteRange(houseA, houseB);
            case STATION:
                return compareLowerIsBetter(houseA.getStationWalkMinutes(), houseB.getStationWalkMinutes());
            case AREA:
                return compareHigherIsBetter(houseA.getArea(), houseB.getArea());
            case OPTION:
                return compareHigherIsBetter(optionRank(houseA.getOptionType()), optionRank(houseB.getOptionType()));
            default:
                throw new IllegalStateException("처리할 수 없는 비교 요소입니다: " + factor);
        }
    }

    /**
     * 지역 기준 예상치의 작은 차이를 우열로 과장하지 않도록 최소·최대가 모두 5분 이상 짧을
     * 때만 우세로 판단한다. 한쪽 끝값이라도 기준을 충족하지 않으면 평균으로 임의 판정하지 않는다.
     */
    private FactorComparison compareCommuteRange(HouseResponse houseA, HouseResponse houseB) {
        Integer aMin = houseA.getCommuteMinMinutes();
        Integer aMax = houseA.getCommuteMaxMinutes();
        Integer bMin = houseB.getCommuteMinMinutes();
        Integer bMax = houseB.getCommuteMaxMinutes();
        if (aMin == null || aMax == null || bMin == null || bMax == null) {
            return FactorComparison.notComparable();
        }
        if ((long) aMin + COMMUTE_ADVANTAGE_MINUTES <= bMin
                && (long) aMax + COMMUTE_ADVANTAGE_MINUTES <= bMax) {
            return FactorComparison.comparable(true);
        }
        if ((long) bMin + COMMUTE_ADVANTAGE_MINUTES <= aMin
                && (long) bMax + COMMUTE_ADVANTAGE_MINUTES <= aMax) {
            return FactorComparison.comparable(false);
        }
        return FactorComparison.notComparable();
    }

    private Long monthlyCost(HouseResponse house) {
        if (house.getMonthlyRent() == null || house.getMaintenanceFee() == null) {
            return null;
        }
        return house.getMonthlyRent() + house.getMaintenanceFee();
    }

    private Integer optionRank(String optionType) {
        return OPTION_RANKS.get(optionType);
    }

    private <T extends Comparable<T>> FactorComparison compareLowerIsBetter(T a, T b) {
        return compareInternal(a, b, true);
    }

    private <T extends Comparable<T>> FactorComparison compareHigherIsBetter(T a, T b) {
        return compareInternal(a, b, false);
    }

    private <T extends Comparable<T>> FactorComparison compareInternal(T a, T b, boolean lowerIsBetter) {
        if (a == null || b == null || a.compareTo(b) == 0) {
            return FactorComparison.notComparable();
        }
        boolean houseABetter = lowerIsBetter ? a.compareTo(b) < 0 : a.compareTo(b) > 0;
        return FactorComparison.comparable(houseABetter);
    }

    private BalanceGameQuestionsResponse toQuestionsResponse(
            List<PreferenceQuestionResponse> served, Map<Long, String> answers) {
        List<BalanceGameQuestionResponse> questions = served.stream()
                .map(question -> new BalanceGameQuestionResponse(
                        question.getId(),
                        question.getOptionAText(),
                        question.getOptionAFactor(),
                        question.getOptionBText(),
                        question.getOptionBFactor(),
                        answers.get(question.getId())))
                .collect(Collectors.toList());
        int answeredCount = countAnswered(served, answers);
        return new BalanceGameQuestionsResponse(
                served.size(), answeredCount, answeredCount == served.size(), questions);
    }

    private BalanceGameProgressResponse toProgressResponse(
            List<PreferenceQuestionResponse> served, Map<Long, String> answers) {
        int answeredCount = countAnswered(served, answers);
        return new BalanceGameProgressResponse(served.size(), answeredCount, answeredCount == served.size());
    }

    private int countAnswered(List<PreferenceQuestionResponse> served, Map<Long, String> answers) {
        return (int) served.stream().filter(question -> answers.containsKey(question.getId())).count();
    }

    private Map<Long, String> answersByQuestionId(Long comparisonId) {
        return houseBalanceGameMapper.findAnswersByComparisonId(comparisonId).stream()
                .collect(Collectors.toMap(
                        PreferenceAnswerResponse::getQuestionId,
                        PreferenceAnswerResponse::getSelectedSide));
    }

    private static final class FactorComparison {

        private final boolean comparable;
        private final boolean houseABetter;

        private FactorComparison(boolean comparable, boolean houseABetter) {
            this.comparable = comparable;
            this.houseABetter = houseABetter;
        }

        static FactorComparison comparable(boolean houseABetter) {
            return new FactorComparison(true, houseABetter);
        }

        static FactorComparison notComparable() {
            return new FactorComparison(false, false);
        }

        boolean isComparable() {
            return comparable;
        }

        boolean isHouseABetter() {
            return houseABetter;
        }
    }
}
