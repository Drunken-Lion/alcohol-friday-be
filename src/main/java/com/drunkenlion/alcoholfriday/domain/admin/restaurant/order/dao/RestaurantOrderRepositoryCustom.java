package com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dao;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity.RestaurantOrder;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface RestaurantOrderRepositoryCustom {
    Page<RestaurantOrder> findRestaurantOrdersByOwner(Member member, Pageable pageable);

    Optional<RestaurantOrder> findRestaurantOrderAddInfo(Long id);
    
    Optional<RestaurantOrder> findRestaurantOrderWaitingApproval(Long id);

    List<RestaurantOrder> findOrderToDelete();
}
