package com.drunkenlion.alcoholfriday.domain.payment.util;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;

public class OrderValidator {
    public static void compareEntityIdToMemberId(Order entity, Member member) {
        if (!entity.getMember().getId().equals(member.getId())) {
            throw new BusinessException(HttpResponse.Fail.INVALID_ACCOUNT);
        }
    }

    public static void checkOrderNo(Order entity, String orderNo) {
        if (!entity.getOrderNo().equals(orderNo)) {
            throw new BusinessException(HttpResponse.Fail.NOT_FOUND_ORDER);
        }
    }
}
