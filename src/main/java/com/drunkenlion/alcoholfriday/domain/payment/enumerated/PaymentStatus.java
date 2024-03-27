package com.drunkenlion.alcoholfriday.domain.payment.enumerated;

import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import lombok.Getter;

import java.util.Arrays;

/**
 * 결제 처리 상태
 */
@Getter
public enum PaymentStatus {
    /**
     * 대기 - 결제를 생성하면 가지게 되는 초기 상태입니다. 인증 전까지는 READY 상태를 유지합니다.
     */
    READY("READY"),
    /**
     * 인증 완료 - 결제수단 정보와 해당 결제수단의 소유자가 맞는지 인증을 마친 상태입니다. 결제 승인 API를 호출하면 결제가 완료됩니다.
     */
    IN_PROGRESS("IN_PROGRESS"),
    /**
     * 가상계좌 대기 - 가상계좌 결제 흐름에만 있는 상태로, 결제 고객이 발급된 가상계좌에 입금하는 것을 기다리고 있는 상태입니다.
     */
    WAITING_FOR_DEPOSIT("WAITING_FOR_DEPOSIT"),
    /**
     * 완료 - 인증된 결제수단 정보, 고객 정보로 요청한 결제가 승인된 상태입니다.
     */
    DONE("DONE"),
    /**
     * 취소 - 승인된 결제가 취소된 상태입니다.
     */
    CANCELED("CANCELED"),
    /**
     * 부분 취소 - 승인된 결제가 부분 취소된 상태입니다.
     */
    PARTIAL_CANCELED("PARTIAL_CANCELED"),
    /**
     * 실패 - 결제 승인이 실패한 상태입니다.
     */
    ABORTED("ABORTED"),
    /**
     * 만료 - 결제 유효 시간 30분이 지나 거래가 취소된 상태입니다.
     *      - IN_PROGRESS 상태에서 결제 승인 API를 호출하지 않으면 EXPIRED가 됩니다.
     */
    EXPIRED("EXPIRED"),
    /**
     * 거절 - 결제가 거부되었거나 실패한 상태입니다. 이는 주로 카드 거래 중 거래 거절, 잔액 부족, 유효하지 않은 카드 등의 이유로 발생합니다.
     */
    DECLINED("DECLINED"),
    /**
     * 환불 - 이미 완료된 결제에 대한 환불이 이루어진 상태입니다. 고객에게 일정 금액이 환불된 경우 해당됩니다.
     */
    REFUNDED("REFUNDED"),
    /**
     * 에러 - 결제 처리 중에 오류가 발생하여 실패한 상태입니다.
     */
    ERROR("ERROR");

    private final String status;

    PaymentStatus(String status) {
        this.status = status;
    }

    public static PaymentStatus ofStatus(String status) {
        if (status == null) {
            return null;
        }

        return Arrays.stream(PaymentStatus.values())
                .filter(value -> value.status.equals(status))
                .findFirst()
                .orElseThrow(() -> new BusinessException(HttpResponse.Fail.NOT_FOUND_PAYMENT_STATUS));
    }
}
