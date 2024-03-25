package com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dao;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.entity.RestaurantOrderRefund;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.enumerated.RestaurantOrderRefundStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantOrderRefundRepository extends JpaRepository<RestaurantOrderRefund, Long>, RestaurantOrderRefundRepositoryCustom {
    Page<RestaurantOrderRefund> findByRestaurantIdAndDeletedAtIsNullOrderByIdDesc(Long restaurantId, Pageable pageable);
    boolean existsByRestaurantOrderIdAndStatusAndDeletedAtIsNull(Long orderId, RestaurantOrderRefundStatus restaurantOrderRefundStatus);
    Optional<RestaurantOrderRefund> findByIdAndDeletedAtIsNull(Long id);
    Page<RestaurantOrderRefund> findByDeletedAtIsNullOrderByIdDesc(Pageable pageable);
}
