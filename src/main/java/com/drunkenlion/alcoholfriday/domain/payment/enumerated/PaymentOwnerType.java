package com.drunkenlion.alcoholfriday.domain.payment.enumerated;

import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import lombok.Getter;

import java.util.Arrays;

/**
 * 카드의 소유자 타입
 */
@Getter
public enum PaymentOwnerType {
    /**
     * 개인
     */
    PERSONAL("개인"),
    /**
     * 법인
     */
    CORPORATION("법인"),
    /**
     * 미확인 - 고객이 해외 카드로 결제했거나 간편결제의 결제 수단을 조합해서 결제했을 때 미확인으로 표시됩니다.
     */
    UNIDENTIFIED("미확인");

    private final String ownerType;

    PaymentOwnerType(String ownerType) {
        this.ownerType = ownerType;
    }

    public static PaymentOwnerType ofOwnerType(String ownerType) {
        if (ownerType == null) {
            return null;
        }

        return Arrays.stream(PaymentOwnerType.values())
                .filter(value -> value.ownerType.equals(ownerType))
                .findFirst()
                .orElseThrow(() -> new BusinessException(HttpResponse.Fail.NOT_FOUND_PAYMENT_OWNER_TYPE));
    }
}
