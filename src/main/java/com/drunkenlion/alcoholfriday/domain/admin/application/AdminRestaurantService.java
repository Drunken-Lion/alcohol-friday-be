package com.drunkenlion.alcoholfriday.domain.admin.application;

import com.drunkenlion.alcoholfriday.domain.admin.dto.RestaurantListResponse;
import org.springframework.data.domain.Page;

public interface AdminRestaurantService {
    Page<RestaurantListResponse> getRestaurants(int page, int size);
}
