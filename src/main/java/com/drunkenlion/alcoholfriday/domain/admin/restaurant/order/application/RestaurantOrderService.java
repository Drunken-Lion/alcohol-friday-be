package com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.application;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.response.OwnerRestaurantOrderListResponse;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import org.springframework.data.domain.Page;

public interface RestaurantOrderService {
    Page<OwnerRestaurantOrderListResponse> getRestaurantOrdersByOwner(Member member, Long restaurantId, int page, int size);
}
