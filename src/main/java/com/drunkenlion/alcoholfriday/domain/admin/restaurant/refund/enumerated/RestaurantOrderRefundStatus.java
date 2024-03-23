package com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.enumerated;

import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum RestaurantOrderRefundStatus {
    WAITING_APPROVAL("환불승인대기", "1"),
    COMPLETED_APPROVAL("환불승인완료", "2"),
    REJECTED_APPROVAL("환불승인반려", "3"),
    COMPLETED("환불완료", "4"),
    CANCELLED("환불취소", "5"),
    ;

    private final String role;
    private final String roleValue;


    RestaurantOrderRefundStatus(String role, String roleValue) {
        this.role = role;
        this.roleValue = roleValue;
    }

    public static RestaurantOrderRefundStatus byStatus(String role) {
        return Arrays.stream(RestaurantOrderRefundStatus.values())
                .filter(value -> value.getRole().equals(role))
                .findFirst()
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_STATUS)
                        .build());
    }

    public static RestaurantOrderRefundStatus byRoleValue(String roleValue) {
        return Arrays.stream(RestaurantOrderRefundStatus.values())
                .filter(value -> value.getRoleValue().equals(roleValue))
                .findAny()
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_STATUS)
                        .build());
    }
}
