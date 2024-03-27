package com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.util;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.dto.request.RestaurantStockModifyRequest;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.RestaurantStock;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;

import java.math.BigDecimal;

public class RestaurantStockValidator {
    public static void validateOwnership(Member member, Restaurant restaurant) {
        if (member.getRole().equals(MemberRole.OWNER)
                && !restaurant.getMember().equals(member)) {
            throw new BusinessException(HttpResponse.Fail.FORBIDDEN);
        }
    }

    public static void validateOwnerModifyQuantity(Member member,
                                                   RestaurantStock stock,
                                                   RestaurantStockModifyRequest modifyRequest) {

        if (member.getRole().equals(MemberRole.OWNER)
                && stock.getQuantity() < modifyRequest.getQuantity()) {
            throw new BusinessException(HttpResponse.Fail.STOCK_ADDITION_FORBIDDEN);
        }
    }

    public static void validateNegative(RestaurantStockModifyRequest modifyRequest) {
        if (modifyRequest.getPrice().compareTo(BigDecimal.ZERO) < 0
                && modifyRequest.getQuantity() < 0) {
            throw new BusinessException(HttpResponse.Fail.PRICE_AND_STOCK_NOT_NEGATIVE);
        }

        if (modifyRequest.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(HttpResponse.Fail.PRICE_NOT_NEGATIVE);
        }

        if (modifyRequest.getQuantity() < 0) {
            throw new BusinessException(HttpResponse.Fail.STOCK_NOT_NEGATIVE);
        }
    }

    public static void validateStockInRestaurant(RestaurantStock stock, Restaurant restaurant) {
        if (!stock.getRestaurant().equals(restaurant)) {
            throw new BusinessException(HttpResponse.Fail.NOT_FOUND_STOCK_IN_RESTAURANT);
        }
    }
}
