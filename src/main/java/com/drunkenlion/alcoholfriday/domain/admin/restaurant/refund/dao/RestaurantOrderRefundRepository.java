package com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dao;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.entity.RestaurantOrderRefund;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.enumerated.RestaurantOrderRefundStatus;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantOrderRefundRepository extends JpaRepository<RestaurantOrderRefund, Long> {
    Page<RestaurantOrderRefund> findByRestaurant(Restaurant restaurant, Pageable pageable);
    boolean existsByRestaurantOrderIdAndStatusAndDeletedAtIsNull(Long orderId, RestaurantOrderRefundStatus restaurantOrderRefundStatus);
    Page<RestaurantOrderRefund> findByRestaurantAndDeletedAtIsNull(Restaurant restaurant, Pageable pageable);
    Optional<RestaurantOrderRefund> findByIdAndDeletedAtIsNull(Long id);
}
