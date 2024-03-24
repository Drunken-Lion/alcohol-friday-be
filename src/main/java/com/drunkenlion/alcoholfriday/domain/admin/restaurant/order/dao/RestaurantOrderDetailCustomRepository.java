package com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dao;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity.RestaurantOrder;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity.RestaurantOrderDetail;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;

import java.util.Optional;

public interface RestaurantOrderDetailCustomRepository {
    Optional<RestaurantOrderDetail> findRestaurantOrderAndProduct(RestaurantOrder order, Product product);
}
