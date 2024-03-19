package com.drunkenlion.alcoholfriday.domain.payment.enumerated;

import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import lombok.Getter;

import java.util.Arrays;

/**
 * 카드사 코드
 * - 발급사는 실물 카드를 발급하는 곳이기 때문에 회원을 관리
 * - 매입사는 카드가 사용된 후 매출 전표를 매입하고 카드 가맹점에 대금을 입금해 주는 가맹점 대상 업무
 */
@Getter
public enum PaymentCardCode {
    /**
     * 기업 BC
     */
    IBK_BC("기업 BC", "3K"),
    /**
     * 광주은행
     */
    GWANGJU_BANK("광주은행", "46"),
    /**
     * 롯데카드
     */
    LOTTE("롯데카드", "71"),
    /**
     * KDB산업은행
     */
    KDB_BANK("KDB산업은행", "30"),
    /**
     * BC카드
     */
    BC("BC카드", "31"),
    /**
     * 삼성카드
     */
    SAMSUNG("삼성카드", "51"),
    /**
     * 새마을금고
     */
    SAEMAUL("새마을금고", "38"),
    /**
     * 신한카드
     */
    SHINHAN("신한카드", "41"),
    /**
     * 신협
     */
    SHINHYEOP("신협", "62"),
    /**
     * 씨티카드
     */
    CITI("씨티카드", "36"),
    /**
     * 우리BC카드(BC 매입)
     */
    WOORI_BC("우리BC카드", "33"),
    /**
     * 우리카드(우리 매입)
     */
    WOORI("우리카드", "W1"),
    /**
     * 우체국예금보험
     */
    POST("우체국예금보험", "37"),
    /**
     * 저축은행중앙회
     */
    SAVING_BANK("저축은행중앙회", "39"),
    /**
     * 전북은행
     */
    JEONBUK_BANK("전북은행", "35"),
    /**
     * 제주은행
     */
    JEJU_BANK("제주은행", "42"),
    /**
     * 카카오뱅크
     */
    KAKAO_BANK("카카오뱅크", "15"),
    /**
     * 케이뱅크
     */
    K_BANK("케이뱅크", "3A"),
    /**
     * 토스뱅크
     */
    TOSS_BANK("토스뱅크", "24"),
    /**
     * 하나카드
     */
    HANA("하나카드", "21"),
    /**
     * 현대카드
     */
    HYUNDAI("현대카드", "61"),
    /**
     * KB국민카드
     */
    KOOKMIN("KB국민카드", "11"),
    /**
     * NH농협카드
     */
    NONGHYEOP("NH농협카드", "91"),
    /**
     * Sh수협은행
     */
    SUHYEOP("Sh수협은행", "34");

    private final String cardName;
    private final String cardCode;

    PaymentCardCode(String cardName, String cardCode) {
        this.cardName = cardName;
        this.cardCode = cardCode;
    }

    public static String ofCardName(PaymentCardCode cardCode) {
        if (cardCode == null) {
            return "";
        }

        for (PaymentCardCode value : PaymentCardCode.values()) {
            if (value.cardCode.equals(cardCode.getCardCode())) {
                return value.cardName;
            }
        }

        throw new BusinessException(HttpResponse.Fail.NOT_FOUND_PAYMENT_CARD_CODE);
    }

    public static PaymentCardCode ofCardCode(String cardCode) {
        if (cardCode == null) {
            return null;
        }

        return Arrays.stream(PaymentCardCode.values())
                .filter(value -> value.cardCode.equals(cardCode))
                .findFirst()
                .orElseThrow(() -> new BusinessException(HttpResponse.Fail.NOT_FOUND_PAYMENT_CARD_CODE));
    }
}
