package com.drunkenlion.alcoholfriday.domain.payment.util;

import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import com.drunkenlion.alcoholfriday.global.common.enumerated.OrderStatus;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;

import java.math.BigDecimal;

public class PaymentValidator {
    public static void orderStatusIsOrderReceived(Order order) {
        if (!order.getOrderStatus().equals(OrderStatus.ORDER_RECEIVED))
            throw new BusinessException(HttpResponse.Fail.ORDER_ISSUE);
    }

    public static void checkTotalPrice(Order order, BigDecimal amount) {
        if (!amount.equals(order.getTotalPrice()))
            throw new BusinessException(HttpResponse.Fail.BAD_REQUEST_AMOUNT);
    }
}
