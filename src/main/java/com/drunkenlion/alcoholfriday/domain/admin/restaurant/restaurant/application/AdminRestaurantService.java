package com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.application;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.dto.RestaurantDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.dto.RestaurantListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.dto.RestaurantRequest;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import org.springframework.data.domain.Page;

public interface AdminRestaurantService {
    Page<RestaurantListResponse> getRestaurants(Member member, int page, int size);

    RestaurantDetailResponse getRestaurant(Member member, Long id);

    RestaurantDetailResponse createRestaurant(Member member, RestaurantRequest restaurantRequest);

    RestaurantDetailResponse modifyRestaurant(Member member, Long id, RestaurantRequest restaurantRequest);

    void deleteRestaurant(Member member, Long id);
}
