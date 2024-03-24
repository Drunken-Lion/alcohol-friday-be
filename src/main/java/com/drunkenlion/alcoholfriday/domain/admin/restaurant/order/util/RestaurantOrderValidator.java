package com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.util;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity.RestaurantOrder;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity.RestaurantOrderDetail;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.enumerated.RestaurantOrderStatus;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.entity.RestaurantOrderCartDetail;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse.Fail;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;

public class RestaurantOrderValidator {

    public static void compareEntityMemberToMember(RestaurantOrder order, Member member) {
        if (!order.getMember().getId().equals(member.getId())) {
            throw new BusinessException(Fail.FORBIDDEN);
        }
    }

    public static void validateOwner(Member member) {
        if (!member.getRole().equals(MemberRole.OWNER)) {
            throw new BusinessException(Fail.FORBIDDEN);
        }
    }

    public static void validateAdmin(Member member) {
        if (!member.getRole().equals(MemberRole.ADMIN)) {
            throw new BusinessException(Fail.FORBIDDEN);
        }
    }

    public static void validateAdminOrStoreManager(Member member) {
        if (!isAdminOrStoreManager(member)) {
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
        if (!restaurant.getMember().getId().equals(member.getId())) {
            throw new BusinessException(Fail.FORBIDDEN);
        }
    }

    private static boolean isAdminOrStoreManager(Member member) {
        return member.getRole().equals(MemberRole.ADMIN) || member.getRole().equals(MemberRole.STORE_MANAGER);
    }
}
