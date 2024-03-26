package com.drunkenlion.alcoholfriday.domain.payment.enumerated;

import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import lombok.Getter;

import java.util.Arrays;

/**
 * 간편결제사 코드
 */
@Getter
public enum PaymentProvider {
    /**
     * 토스페이
     */
    TOSS_PAY("토스페이"),
    /**
     * 네이버페이
     */
    NAVER_PAY("네이버페이"),
    /**
     * 삼성페이
     */
    SAMSUNG_PAY("삼성페이"),
    /**
     * 애플페이
     */
    APPLE_PAY("애플페이"),
    /**
     * 엘페이
     */
    L_PAY("엘페이"),
    /**
     * 카카오페이
     */
    KAKAO_PAY("카카오페이"),
    /**
     * 핀페이
     */
    PIN_PAY("핀페이"),
    /**
     * 페이코
     */
    PAYCO("페이코"),
    /**
     * SSG페이
     */
    SSG_PAY("SSG페이");

    private final String paymentProvider;

    PaymentProvider(String paymentProvider) {
        this.paymentProvider = paymentProvider;
    }

    public static PaymentProvider ofPaymentProvider(String paymentProvider) {
        if (paymentProvider == null) {
            return null;
        }

        return Arrays.stream(PaymentProvider.values())
                .filter(value -> value.paymentProvider.equals(paymentProvider))
                .findFirst()
                .orElseThrow(() -> new BusinessException(HttpResponse.Fail.NOT_FOUND_PAYMENT_PROVIDER));
    }
}
