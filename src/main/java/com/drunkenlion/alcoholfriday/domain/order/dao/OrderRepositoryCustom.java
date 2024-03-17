package com.drunkenlion.alcoholfriday.domain.order.dao;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepositoryCustom {
    Page<Order> findMyOrderList(Member member, Pageable pageable);
}
