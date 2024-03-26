package com.drunkenlion.alcoholfriday.domain.payment.enumerated;

import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import lombok.Getter;

import java.util.Arrays;

/**
 * 결제 수단
 */
@Getter
public enum PaymentMethod {
    /**
     * 카드
     */
    CARD("카드"),
    /**
     * 가상계좌
     */
    VIRTUAL_ACCOUNT("가상계좌"),
    /**
     * 간편결제
     */
    EASY_PAYMENT("간편결제"),
    /**
     * 휴대폰
     */
    CELLPHONE("휴대폰"),
    /**
     * 계좌이체
     */
    ACCOUNT_TRANSFER("계좌이체"),
    /**
     * 문화상품권
     */
    CULTURE_GIFT("문화상품권"),
    /**
     * 도서문화상품권
     */
    BOOK_CULTURE_GIFT("도서문화상품권"),
    /**
     * 게임문화상품권
     */
    GAME_CULTURE_GIFT("게임문화상품권");

    private final String method;

    PaymentMethod(String method) {
        this.method = method;
    }

    public static PaymentMethod ofMethod(String method) {
        if (method == null) {
            return null;
        }

        return Arrays.stream(PaymentMethod.values())
                .filter(value -> value.method.equals(method))
                .findFirst()
                .orElseThrow(() -> new BusinessException(HttpResponse.Fail.NOT_FOUND_PAYMENT_METHOD));
    }
}
