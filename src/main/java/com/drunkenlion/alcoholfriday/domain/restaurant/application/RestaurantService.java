package com.drunkenlion.alcoholfriday.domain.restaurant.application;

import com.drunkenlion.alcoholfriday.domain.restaurant.dto.response.RestaurantLocationResponse;


import java.util.List;

public interface RestaurantService {
    List<RestaurantLocationResponse> getRestaurants(double neLatitude,
                                                    double neLongitude,
                                                    double swLatitude,
                                                    double swLongitude);
}
