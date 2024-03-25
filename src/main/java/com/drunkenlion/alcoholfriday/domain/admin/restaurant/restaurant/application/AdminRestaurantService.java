package com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.application;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.dto.response.RestaurantDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.dto.response.RestaurantListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.dto.request.RestaurantRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.dto.response.RestaurantStockListResponse;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import org.springframework.data.domain.Page;

public interface AdminRestaurantService {
    Page<RestaurantListResponse> getRestaurants(Member member, int page, int size);

    RestaurantDetailResponse getRestaurant(Member member, Long id);

    RestaurantDetailResponse createRestaurant(Member member, RestaurantRequest restaurantRequest);

    RestaurantDetailResponse modifyRestaurant(Member member, Long id, RestaurantRequest restaurantRequest);

    void deleteRestaurant(Member member, Long id);

    Page<RestaurantStockListResponse> getRestaurantStocks(Member member, Long restaurantId, int page, int size);
}
