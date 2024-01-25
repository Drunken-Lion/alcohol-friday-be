package com.drunkenlion.alcoholfriday.global.common.enumerated;

/**
 * 주문 상태
 */
public enum OrderStatus {
    /**
     * 결제 완료 - 주문에 대한 결제가 완료되었으며 처리가 진행 중인 상태입니다.
     */
    PAYMENT_COMPLETED,
    /**
     * 배송 준비 - 제품이 포장되고 출고 준비되는 상태입니다.
     */
    READY_FOR_SHIPMENT,
    /**
     * 배송 중 - 주문이 배송 중이며 고객에게 전송 중인 상태입니다.
     */
    SHIPPED,
    /**
     * 배송 완료 - 주문이 성공적으로 고객에게 전달되었으며 완료된 상태입니다.
     */
    DELIVERED,
    /**
     * 주문 취소 - 주문이 고객이나 시스템에 의해 취소된 상태입니다.
     */
    CANCELLED,
    /**
     * 환불 처리 - 주문이 취소되어 환불이 처리 중인 상태입니다.
     */
    REFUND_PROCESSING,
    /**
     * 환불 완료 - 환불이 완료되어 주문이 종료된 상태입니다.
     */
    REFUND_COMPLETED,
    /**
     * 문제 발생 - 주문 처리 중에 문제가 발생하여 추가 조치가 필요한 상태입니다.
     */
    ISSUE_DETECTED
}
