package com.drunkenlion.alcoholfriday.global.common.enumerated;

/**
 * 주문 상태
 */
public enum OrderStatus {
    /**
     * 주문 접수 - 고객이 주문을 제출한 초기 상태로, 시스템이 주문을 받아들입니다.
     */
    ORDER_RECEIVED,
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
     * 주문 취소 - 주문이 고객에 의해 취소된 상태입니다.
     */
    CANCELLED,
    /**
     * 주문 취소 완료 - 취소된 주문을 관리자가 취소 완료시킨 상태입니다.
     */
    CANCEL_COMPLETED,
    /**
     * 환불 처리 - 주문이 고객에 의해 환불이 처리 중인 상태입니다.
     */
    REFUND_PROCESSING,
    /**
     * 환불 완료 - 환불 처리된 주문을 관리자가 환불 완료시킨 상태입니다.
     */
    REFUND_COMPLETED,
    /**
     * 문제 발생 - 주문 처리 중에 문제가 발생하여 추가 조치가 필요한 상태입니다.
     */
    ISSUE_DETECTED
}
