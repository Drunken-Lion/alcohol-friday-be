package com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.application;

import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dto.response.RestaurantDetailProductResponse;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dto.response.RestaurantDetailResponse;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dto.response.RestaurantMapResponse;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dto.response.RestaurantNearbyResponse;
import org.springframework.data.domain.Page;


import java.util.List;

public interface RestaurantService {
    List<RestaurantMapResponse> findRestaurantInMap(double neLatitude,
                                                    double neLongitude,
                                                    double swLatitude,
                                                    double swLongitude);
    Page<RestaurantNearbyResponse> findRestaurantWithItem(double userLocationLatitude,
                                                          double userLocationLongitude,
                                                          Long itemId,
                                                          int page,
                                                          int size);

    RestaurantDetailResponse findRestaurant(Long id);

    Page<RestaurantDetailProductResponse> findRestaurantStock(Long restaurantId,
                                                              int page,
                                                              int size);
}
