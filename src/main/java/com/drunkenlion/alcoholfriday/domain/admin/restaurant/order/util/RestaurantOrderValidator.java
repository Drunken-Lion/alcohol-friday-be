package com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.util;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;

public class RestaurantOrderValidator {
    public static void validateOwnerRole(Member member) {
        if (!member.getRole().equals(MemberRole.OWNER)) {
            throw new BusinessException(HttpResponse.Fail.FORBIDDEN);
        }
    }
}
