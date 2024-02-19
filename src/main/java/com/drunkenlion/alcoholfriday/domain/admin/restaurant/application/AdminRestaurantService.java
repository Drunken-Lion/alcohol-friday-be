package com.drunkenlion.alcoholfriday.domain.admin.restaurant.application;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.dto.RestaurantRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.dto.RestaurantDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.dto.RestaurantListResponse;
import org.springframework.data.domain.Page;

public interface AdminRestaurantService {
    Page<RestaurantListResponse> getRestaurants(int page, int size);

    RestaurantDetailResponse getRestaurant(Long id);

    RestaurantDetailResponse createRestaurant(RestaurantRequest restaurantRequest);

    RestaurantDetailResponse modifyRestaurant(Long id, RestaurantRequest restaurantRequest);

    void deleteRestaurant(Long id);
}
