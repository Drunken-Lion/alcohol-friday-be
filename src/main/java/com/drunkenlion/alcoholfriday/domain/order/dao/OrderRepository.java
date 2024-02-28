package com.drunkenlion.alcoholfriday.domain.order.dao;

import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);
}
