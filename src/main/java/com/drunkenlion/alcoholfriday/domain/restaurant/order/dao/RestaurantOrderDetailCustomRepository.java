package com.drunkenlion.alcoholfriday.domain.restaurant.order.dao;

import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.domain.restaurant.order.entity.RestaurantOrder;
import com.drunkenlion.alcoholfriday.domain.restaurant.order.entity.RestaurantOrderDetail;
import java.util.Optional;

public interface RestaurantOrderDetailCustomRepository {
    Optional<RestaurantOrderDetail> findRestaurantOrderAndProduct(RestaurantOrder order, Product product);
}
