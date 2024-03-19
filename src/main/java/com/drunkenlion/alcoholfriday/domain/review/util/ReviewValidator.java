package com.drunkenlion.alcoholfriday.domain.review.util;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import com.drunkenlion.alcoholfriday.domain.review.entity.Review;
import com.drunkenlion.alcoholfriday.global.common.enumerated.OrderStatus;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse.Fail;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import java.util.Optional;

public class ReviewValidator {
    public static void compareEntityIdToMemberId(Order entity, Member member) {
        if (!entity.getMember().getId().equals(member.getId())) {
            throw new BusinessException(Fail.FORBIDDEN);
        }
    }

    public static void compareEntityIdToMemberId(Review entity, Member member) {
        if (!entity.getMember().getId().equals(member.getId())) {
            throw new BusinessException(Fail.FORBIDDEN);
        }
    }

    public static void checkedStatus(Order order) {
        if (!order.getOrderStatus().equals(OrderStatus.DELIVERED)) {
            throw new BusinessException(Fail.BAD_REQUEST);
        }
    }
}
