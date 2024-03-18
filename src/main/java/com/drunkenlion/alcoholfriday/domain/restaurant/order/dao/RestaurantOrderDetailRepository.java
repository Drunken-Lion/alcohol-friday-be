package com.drunkenlion.alcoholfriday.domain.restaurant.order.dao;

import com.drunkenlion.alcoholfriday.domain.restaurant.order.entity.RestaurantOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantOrderDetailRepository extends JpaRepository<RestaurantOrderDetail, Long> {
}
