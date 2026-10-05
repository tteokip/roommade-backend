package com.roommade.domain.preparation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 금액 필드는 원 단위로 조회한다. */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DepositProgressSourceResponse {

    private Long targetDeposit;
    private Long currentDeposit;
}
