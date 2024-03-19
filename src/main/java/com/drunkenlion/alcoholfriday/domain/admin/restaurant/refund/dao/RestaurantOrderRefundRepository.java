package com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dao;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.entity.RestaurantOrderRefund;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantOrderRefundRepository extends JpaRepository<RestaurantOrderRefund, Long> {
}
