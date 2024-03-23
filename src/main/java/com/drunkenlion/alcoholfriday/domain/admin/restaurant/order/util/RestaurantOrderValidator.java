package com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.util;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity.RestaurantOrderDetail;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.domain.restaurant.cart.entity.RestaurantOrderCartDetail;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse.Fail;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;

public class RestaurantOrderValidator {
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
}
