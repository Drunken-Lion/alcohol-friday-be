package com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dao;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.entity.RestaurantOrderRefundDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantOrderRefundDetailRepository extends JpaRepository<RestaurantOrderRefundDetail, Long> {
}
