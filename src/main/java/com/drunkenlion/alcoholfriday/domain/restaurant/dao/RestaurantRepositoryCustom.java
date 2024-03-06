package com.drunkenlion.alcoholfriday.domain.restaurant.dao;

import com.drunkenlion.alcoholfriday.domain.restaurant.entity.Restaurant;

import java.util.List;

public interface RestaurantRepositoryCustom {

    List<Restaurant> getRestaurant(double neLatitude, double neLongitude, double swLatitude, double swLongitude);
}
