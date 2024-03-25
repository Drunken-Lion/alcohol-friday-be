package com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.application;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dto.response.RestaurantOrderProductListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.response.OwnerRestaurantOrderListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.response.RestaurantOrderListResponse;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import org.springframework.data.domain.Page;

public interface RestaurantOrderService {
    Page<RestaurantOrderListResponse> getRestaurantOrdersByAdminOrStoreManager(Member member, int page, int size);

    Page<RestaurantOrderProductListResponse> getRestaurantOrderProducts(int page, int size, Member member);

    Page<OwnerRestaurantOrderListResponse> getRestaurantOrdersByOwner(Member member, Long restaurantId, int page, int size);
}
