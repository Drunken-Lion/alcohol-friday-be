package com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dao;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.entity.RestaurantOrderRefund;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.entity.RestaurantOrderRefundDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantOrderRefundDetailRepository extends JpaRepository<RestaurantOrderRefundDetail, Long> {
    List<RestaurantOrderRefundDetail> findByRestaurantOrderRefundAndDeletedAtIsNull(RestaurantOrderRefund refund);
}
