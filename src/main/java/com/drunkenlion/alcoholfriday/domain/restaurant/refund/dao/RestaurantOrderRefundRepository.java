package com.drunkenlion.alcoholfriday.domain.restaurant.refund.dao;

import com.drunkenlion.alcoholfriday.domain.restaurant.refund.entity.RestaurantOrderRefund;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantOrderRefundRepository extends JpaRepository<RestaurantOrderRefund, Long>, RestaurantOrderRefundRepositoryCustom {
}
