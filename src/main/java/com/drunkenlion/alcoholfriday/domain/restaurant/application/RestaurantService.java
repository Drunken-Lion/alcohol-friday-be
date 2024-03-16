package com.drunkenlion.alcoholfriday.domain.restaurant.application;

import com.drunkenlion.alcoholfriday.domain.restaurant.dto.response.RestaurantLocationResponse;
import com.drunkenlion.alcoholfriday.domain.restaurant.dto.response.RestaurantNearbyResponse;
import org.springframework.data.domain.Page;


import java.util.List;

public interface RestaurantService {
    List<RestaurantLocationResponse> getRestaurants(double neLatitude,
                                                    double neLongitude,
                                                    double swLatitude,
                                                    double swLongitude);
    Page<RestaurantNearbyResponse> get(double userLocationLatitude, double userLocationLongitude, String keyword, int page, int size);
}
