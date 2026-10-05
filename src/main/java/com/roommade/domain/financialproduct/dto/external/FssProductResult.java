package com.roommade.domain.financialproduct.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FssProductResult {
    @JsonProperty("err_cd") private String errorCode;
    @JsonSetter(value = "baseList", nulls = Nulls.AS_EMPTY) private List<FssProduct> baseList = new ArrayList<>();
    @JsonSetter(value = "optionList", nulls = Nulls.AS_EMPTY) private List<FssProductOption> optionList = new ArrayList<>();
}
