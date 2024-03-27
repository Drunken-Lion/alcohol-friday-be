package com.drunkenlion.alcoholfriday.domain.order.util;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import com.drunkenlion.alcoholfriday.global.common.enumerated.OrderStatus;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;

public class OrderValidator {
    public static void compareEntityIdToMemberId(Order entity, Member member) {
        if (!entity.getMember().getId().equals(member.getId())) {
            throw new BusinessException(HttpResponse.Fail.FORBIDDEN);
        }
    }

    public static void checkOrderNo(Order entity, String orderNo) {
        if (!entity.getOrderNo().equals(orderNo)) {
            throw new BusinessException(HttpResponse.Fail.NOT_FOUND_ORDER);
        }
    }

    public static void checkOrderStatusAbleCancel(Order order) {
        if (!order.getOrderStatus().equals(OrderStatus.PAYMENT_COMPLETED) &&
                !order.getOrderStatus().equals(OrderStatus.READY_FOR_SHIPMENT)) {
            throw new BusinessException(HttpResponse.Fail.ORDER_CANCEL_FAIL);
        }
    }

    public static void checkOrderStatusAbleCancelComplete(Order order) {
        if (!order.getOrderStatus().equals(OrderStatus.CANCELLED)) {
            throw new BusinessException(HttpResponse.Fail.ORDER_CANCEL_COMPLETE_FAIL);
        }
    }
}
