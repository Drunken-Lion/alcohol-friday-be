package com.drunkenlion.alcoholfriday.domain.restaurant.order.util;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.restaurant.order.entity.RestaurantOrder;
import com.drunkenlion.alcoholfriday.domain.restaurant.order.enumerated.RestaurantOrderStatus;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse.Fail;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;

public class RestaurantOrderOwnerValidator {
    public static void compareEntityMemberToMember(RestaurantOrder order, Member member) {
        if (!order.getMember().getId().equals(member.getId())) {
            throw new BusinessException(Fail.FORBIDDEN);
        }
    }

    public static void isAdmin(Member member) {
        if (!member.getRole().equals(MemberRole.ADMIN)) {
            throw new BusinessException(Fail.FORBIDDEN);
        }
    }

    public static void restaurantOrderStatusIsApproval(RestaurantOrder order) {
        if (!order.getOrderStatus().equals(RestaurantOrderStatus.WAITING_APPROVAL)) {
            throw new BusinessException(Fail.BAD_REQUEST);
        }
    }
}
