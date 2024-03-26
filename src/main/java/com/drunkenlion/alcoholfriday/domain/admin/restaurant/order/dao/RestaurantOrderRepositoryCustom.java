package com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dao;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity.RestaurantOrder;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface RestaurantOrderRepositoryCustom {
    Page<RestaurantOrder> findAllRestaurantOrders(Pageable pageable);

    Page<RestaurantOrder> findRestaurantOrdersByOwner(Member member, Restaurant restaurant, Pageable pageable);

    Optional<RestaurantOrder> findRestaurantOrderAddInfo(Long id);

    Optional<RestaurantOrder> findRestaurantOrderWaitingApproval(Long id);

    List<RestaurantOrder> findOrderToDelete();
}
