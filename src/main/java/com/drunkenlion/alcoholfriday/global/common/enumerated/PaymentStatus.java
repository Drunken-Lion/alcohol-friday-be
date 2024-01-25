package com.drunkenlion.alcoholfriday.global.common.enumerated;

/**
 * 결제 상태
 */
public enum PaymentStatus {
    /**
     * 완료 - 결제가 성공적으로 완료되어 거래가 성공적으로 처리된 상태입니다.
     */
    COMPLETED,
    /**
     * 거절 - 결제가 거부되었거나 실패한 상태입니다. 이는 주로 카드 거래 중 거래 거절, 잔액 부족, 유효하지 않은 카드 등의 이유로 발생합니다.
     */
    DECLINED,
    /**
     * 취소 - 이미 완료된 결제를 취소한 상태입니다. 주문이나 거래가 취소되어 결제도 함께 취소되는 경우가 포함됩니다.
     */
    CANCELLED,
    /**
     * 환불 - 이미 완료된 결제에 대한 환불이 이루어진 상태입니다. 고객에게 일정 금액이 환불된 경우 해당됩니다.
     */
    REFUNDED,
    /**
     * 에러 - 결제 처리 중에 오류가 발생하여 실패한 상태입니다.
     */
    ERROR
}
