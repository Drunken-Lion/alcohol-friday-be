package com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.util;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.dto.request.RestaurantStockModifyRequest;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.RestaurantStock;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;

public class RestaurantStockValidator {
    public static void validateOwnerModifyQuantity(RestaurantStock stock, RestaurantStockModifyRequest modifyRequest) {
        if (stock.getQuantity() < modifyRequest.getQuantity()) {
            throw new BusinessException(HttpResponse.Fail.STOCK_ADDITION_FORBIDDEN);
        }
    }

    public static void validateNegativeQuantity(RestaurantStockModifyRequest modifyRequest) {
        if (modifyRequest.getQuantity() < 0) {
            throw new BusinessException(HttpResponse.Fail.STOCK_NOT_NEGATIVE);
        }
    }
}
