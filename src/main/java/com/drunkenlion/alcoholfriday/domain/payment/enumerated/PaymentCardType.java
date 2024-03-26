package com.drunkenlion.alcoholfriday.domain.payment.enumerated;

import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import lombok.Getter;

import java.util.Arrays;

/**
 * 카드 종류
 */
@Getter
public enum PaymentCardType {
    /**
     * 신용
     */
    CREDIT("신용"),
    /**
     * 체크
     */
    CHECK("체크"),
    /**
     * 기프트
     */
    GIFT("기프트"),
    /**
     * 미확인 - 고객이 해외 카드로 결제했거나 간편결제의 결제 수단을 조합해서 결제했을 때 미확인으로 표시됩니다.
     */
    UNIDENTIFIED("미확인");

    private final String cardType;

    PaymentCardType(String cardType) {
        this.cardType = cardType;
    }

    public static PaymentCardType ofCardType(String cardType) {
        if (cardType == null) {
            return null;
        }

        return Arrays.stream(PaymentCardType.values())
                .filter(value -> value.cardType.equals(cardType))
                .findFirst()
                .orElseThrow(() -> new BusinessException(HttpResponse.Fail.NOT_FOUND_PAYMENT_CARD_TYPE));
    }
}
