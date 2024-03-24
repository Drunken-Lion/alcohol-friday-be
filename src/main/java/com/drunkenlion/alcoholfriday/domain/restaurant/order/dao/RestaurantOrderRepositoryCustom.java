package com.drunkenlion.alcoholfriday.domain.restaurant.order.dao;

import com.drunkenlion.alcoholfriday.domain.restaurant.order.entity.RestaurantOrder;
import java.util.List;
import java.util.Optional;

public interface RestaurantOrderRepositoryCustom {
    Optional<RestaurantOrder> findRestaurantOrderAddInfo(Long id);
    Optional<RestaurantOrder> findRestaurantOrderWaitingApproval(Long id);
    List<RestaurantOrder> findOrderToDelete();
}
