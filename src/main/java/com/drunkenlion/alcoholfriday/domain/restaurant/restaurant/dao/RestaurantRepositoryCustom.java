package com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dao;

import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dto.response.RestaurantNearbyResponse;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RestaurantRepositoryCustom {
    Page<Restaurant> findAllBasedAuth(Member member, Pageable pageable);

    List<Restaurant> getRestaurant(double neLatitude, double neLongitude, double swLatitude, double swLongitude);

    Page<RestaurantNearbyResponse> getRestaurantSellingProducts(double userLocationLatitude, double userLocationLongitude, Item item, Pageable pageable);
}
