package com.drunkenlion.alcoholfriday.domain.restaurant.refund.dao;

import com.drunkenlion.alcoholfriday.domain.restaurant.refund.entity.RestaurantOrderRefundDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantOrderRefundDetailRepository extends JpaRepository<RestaurantOrderRefundDetail, Long> {
}
