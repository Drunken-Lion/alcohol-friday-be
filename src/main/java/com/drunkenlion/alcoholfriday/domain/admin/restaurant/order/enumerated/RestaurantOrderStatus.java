package com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.enumerated;

import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import lombok.Getter;

import java.util.Arrays;

/**
 * 매장 주문 상태
 */
@Getter
public enum RestaurantOrderStatus {
    /**
     * 주문 정보 입력
     */
    ADD_INFO("주문정보입력", "1"),
    /**
     * 주문 승인 대기
     */
    WAITING_APPROVAL("주문승인대기", "2"),
    /**
     * 주문 승인 완료
     */
    COMPLETED_APPROVAL("주문승인완료", "3"),
    /**
     * 주문 승인 반려
     */
    REJECTED_APPROVAL("주문승인반려", "4"),
    /**
     * 주문 완료
     */
    COMPLETED("주문완료", "5"),
    /**
     * 주문 취소
     */
    CANCELLED("주문취소", "6");

    private final String name;
    private final String number;

    RestaurantOrderStatus(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public static RestaurantOrderStatus byNumber(String number) {
        return Arrays.stream(RestaurantOrderStatus.values())
                .filter(value -> value.getNumber().equals(number))
                .findAny()
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_RESTAURANT_ORDER_NUMBER)
                        .build());
    }
}
