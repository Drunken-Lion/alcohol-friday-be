package com.drunkenlion.alcoholfriday.domain.admin.restaurant.util;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.entity.RestaurantOrderCartDetail;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity.RestaurantOrder;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity.RestaurantOrderDetail;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.enumerated.RestaurantOrderStatus;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse.Fail;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;

public class RestaurantValidator {

    public static void compareEntityMemberToMember(RestaurantOrder order, Member member) {
        if (!order.getMember().equals(member)) {
            throw new BusinessException(Fail.FORBIDDEN);
        }
    }

    public static void restaurantOrderStatusIsApproval(RestaurantOrder order) {
        if (!order.getOrderStatus().equals(RestaurantOrderStatus.WAITING_APPROVAL)) {
            throw new BusinessException(Fail.BAD_REQUEST);
        }
    }

    public static void checkedQuantity(Product product, Long orderQuantity) {
        if (!(product.getQuantity() - orderQuantity >= 0)) {
            throw new BusinessException(Fail.OUT_OF_ITEM_STOCK);
        }
    }

    public static Long checkedQuantity(RestaurantOrderDetail orderDetail, RestaurantOrderCartDetail cartDetail) {
        if (orderDetail.getQuantity() - cartDetail.getQuantity() < 0) {
            return cartDetail.getQuantity();
        }

        return orderDetail.getQuantity();
    }

    public static void validateOwnership(Member member, Restaurant restaurant) {
        if (!restaurant.getMember().equals(member)) {
            throw new BusinessException(Fail.FORBIDDEN);
        }
    }
}
