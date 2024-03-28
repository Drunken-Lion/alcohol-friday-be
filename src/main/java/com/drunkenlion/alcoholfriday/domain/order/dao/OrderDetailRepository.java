package com.drunkenlion.alcoholfriday.domain.order.dao;

import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import com.drunkenlion.alcoholfriday.domain.order.entity.OrderDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long>, OrderDetailCustomRepository {
    Page<OrderDetail> findByOrderMemberIdAndReviewIsNull(Long memberId, Pageable pageable);
    List<OrderDetail> findByOrderAndDeletedAtIsNull(Order order);
    boolean existsByOrderAndDeletedAtIsNotNull(Order order);
}
