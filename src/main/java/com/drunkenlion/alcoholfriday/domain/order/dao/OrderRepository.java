package com.drunkenlion.alcoholfriday.domain.order.dao;

import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import com.drunkenlion.alcoholfriday.global.common.enumerated.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);
    Page<Order> findAllByOrderStatus(Pageable pageable, OrderStatus status);
    Optional<Order> findByIdAndDeletedAtIsNull(Long id);
}
