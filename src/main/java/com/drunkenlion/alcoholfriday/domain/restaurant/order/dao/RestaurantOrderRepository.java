package com.drunkenlion.alcoholfriday.domain.restaurant.order.dao;

import com.drunkenlion.alcoholfriday.domain.restaurant.order.entity.RestaurantOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantOrderRepository extends JpaRepository<RestaurantOrder, Long>, RestaurantOrderRepositoryCustom {
    Optional<RestaurantOrder> findByIdAndDeletedAtIsNull(Long orderId);
}
