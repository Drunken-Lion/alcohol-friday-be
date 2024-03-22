package com.drunkenlion.alcoholfriday.domain.restaurant.order.dao;

import com.drunkenlion.alcoholfriday.domain.restaurant.order.entity.RestaurantOrder;
import java.util.List;
import java.util.Optional;

public interface RestaurantOrderRepositoryCustom {
    Optional<RestaurantOrder> findRestaurantOrderOwner(Long id);
    Optional<RestaurantOrder> findRestaurantOrderAdmin(Long id);
    List<RestaurantOrder> findOrderToDelete();
}
