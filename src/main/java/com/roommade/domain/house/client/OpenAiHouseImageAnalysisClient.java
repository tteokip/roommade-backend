package com.roommade.domain.house.client;

import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletionContentPart;
import com.openai.models.chat.completions.ChatCompletionContentPartImage;
import com.openai.models.chat.completions.ChatCompletionContentPartText;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.chat.completions.StructuredChatCompletion;
import com.openai.models.chat.completions.StructuredChatCompletionCreateParams;
import com.roommade.domain.house.code.HouseErrorCode;
import com.roommade.global.exception.BusinessException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
public class OpenAiHouseImageAnalysisClient implements HouseImageAnalysisClient {

    private static final String INSTRUCTION = """
            첨부된 1~3장의 이미지는 동일한 부동산 매물의 스크린샷이다.
            화면에 명시적으로 표시된 정보만 근거로 하나의 매물 정보를 추출하라.
            광고, 추천 매물, 주변 매물과 같이 대상 매물과 무관한 정보는 제외하라.

            필드별 추출 규칙은 다음과 같다.
            - location: 도로명 또는 지번 주소를 화면에 표시된 범위까지만 적는다. 주소를 추측하거나 보완하지 않는다.
            - deposit: 보증금을 원 단위 정수로 변환한다. 예: 1억은 100000000, 500만원은 5000000이다.
            - monthlyRent: 월세를 원 단위 정수로 변환한다. 예: 50만원은 500000이다.
            - maintenanceFee: 관리비를 원 단위 정수로 변환한다. 예: 7만원은 70000이다. 별도 관리비가 없다고 명시된 경우에만 0이다.
              monthlyRent와 maintenanceFee는 월 단위로 명시된 값만 반환하고, 주·일 단위 금액은 환산하지 말고 null로 둔다.
            - area: 전용면적을 제곱미터 단위로 반환한다. 평 단위만 있으면 1평=3.3058제곱미터로 변환한다.
            - stationWalkMinutes: 가장 가까운 역까지의 도보 시간만 분 단위 정수로 반환한다.
            - floorType: 대상 매물의 층수 또는 층 유형을 화면 표기대로 간결하게 반환한다.
            - roomStructure: 원룸, 투룸 등 대상 매물의 방 구조를 반환한다.
            - optionType: 화면에 '풀옵션', '부분옵션', '옵션 없음' 중 하나가 명시된 경우에만 해당 값을 반환한다.
              냉장고, 세탁기 등 개별 옵션 목록만 보고 옵션 유형을 추측하지 않는다.
              옵션 유형이 명시되지 않았으면 null로 둔다.

            commuteMinutes는 추출하거나 추측하지 않는다.
            여러 이미지의 정보가 충돌하면 더 명확하게 표시된 값을 사용하고, 판단할 수 없으면 null로 둔다.
            필수 항목인 location, deposit, monthlyRent도 이미지에서 확인되지 않으면 값을 만들어내지 말고 null로 둔다.
            숫자 필드에는 단위나 설명을 붙이지 말고 숫자만 반환한다.
            """;

    private final OpenAIClient openAiClient;

    public OpenAiHouseImageAnalysisClient(@Lazy OpenAIClient openAiClient) {
        this.openAiClient = openAiClient;
    }

    @Override
    public HouseImageExtraction analyze(List<MultipartFile> images) {
        StructuredChatCompletionCreateParams<HouseImageExtraction> params = ChatCompletionCreateParams.builder()
                .model(ChatModel.GPT_4O)
                .addUserMessageOfArrayOfContentParts(toContentParts(images))
                .responseFormat(HouseImageExtraction.class)
                .build();

        try {
            StructuredChatCompletion<HouseImageExtraction> completion =
                    openAiClient.chat().completions().create(params);
            return completion.choices().get(0).message().content()
                    .orElseThrow(() -> new BusinessException(HouseErrorCode.HOUSE_ANALYSIS_FAILED));
        } catch (BusinessException e) {
            throw e;
        } catch (RuntimeException e) {
            log.error("OpenAI 매물 이미지 분석 호출 실패", e);
            throw new BusinessException(HouseErrorCode.HOUSE_ANALYSIS_FAILED);
        }
    }

    private List<ChatCompletionContentPart> toContentParts(List<MultipartFile> images) {
        List<ChatCompletionContentPart> contentParts = new ArrayList<>();
        contentParts.add(ChatCompletionContentPart.ofText(
                ChatCompletionContentPartText.builder().text(INSTRUCTION).build()));
        for (MultipartFile image : images) {
            contentParts.add(ChatCompletionContentPart.ofImageUrl(
                    ChatCompletionContentPartImage.builder()
                            .imageUrl(ChatCompletionContentPartImage.ImageUrl.builder()
                                    .url(toDataUrl(image))
                                    .build())
                            .build()));
        }
        return contentParts;
    }

    private String toDataUrl(MultipartFile image) {
        try {
            String base64 = Base64.getEncoder().encodeToString(image.getBytes());
            return "data:" + image.getContentType() + ";base64," + base64;
        } catch (IOException e) {
            throw new BusinessException(HouseErrorCode.HOUSE_ANALYSIS_FAILED);
        }
    }
}
