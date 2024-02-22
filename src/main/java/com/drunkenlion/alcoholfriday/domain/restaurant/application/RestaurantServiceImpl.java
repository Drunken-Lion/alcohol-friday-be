package com.drunkenlion.alcoholfriday.domain.restaurant.application;

import com.drunkenlion.alcoholfriday.domain.restaurant.dao.RestaurantRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.dto.response.RestaurantLocationResponse;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.Restaurant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService{

    private final RestaurantRepository restaurantRepository;

    @Override
    public List<RestaurantLocationResponse> getRestaurants(double neLatitude, double neLongitude, double swLatitude, double swLongitude) {

        List<Restaurant> allWithinPolygon = restaurantRepository.findAllWithinPolygon(neLatitude, neLongitude, swLatitude, swLongitude);

        return allWithinPolygon.stream().map(RestaurantLocationResponse::of).collect(Collectors.toList());
    }
}
