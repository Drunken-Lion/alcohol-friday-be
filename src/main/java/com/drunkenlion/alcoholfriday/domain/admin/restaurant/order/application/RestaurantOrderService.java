package com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.application;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.RestaurantOrderListResponse;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import org.springframework.data.domain.Page;

public interface RestaurantOrderService {
    Page<RestaurantOrderListResponse> getRestaurantOrdersByOwner(Member member, int page, int size);
}
