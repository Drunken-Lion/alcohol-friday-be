package com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dao;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity.RestaurantOrder;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.entity.RestaurantOrderRefund;

import java.util.List;

public interface RestaurantOrderRefundRepositoryCustom {
    List<RestaurantOrderRefund> findRefundByRestaurantOrderId(RestaurantOrder restaurantOrder);
}
