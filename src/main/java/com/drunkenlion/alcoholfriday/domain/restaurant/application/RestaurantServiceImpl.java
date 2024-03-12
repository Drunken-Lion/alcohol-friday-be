package com.drunkenlion.alcoholfriday.domain.restaurant.application;

import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.domain.restaurant.dao.RestaurantRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.dto.response.RestaurantLocationResponse;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.RestaurantStock;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import com.drunkenlion.alcoholfriday.global.file.application.FileService;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import static com.drunkenlion.alcoholfriday.domain.restaurant.util.RestaurantTimeVerification.getRestaurantBusinessStatus;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final FileService fileService;

    @Override
    public List<RestaurantLocationResponse> getRestaurants(double neLatitude, double neLongitude, double swLatitude, double swLongitude) {

        List<Restaurant> get = restaurantRepository.getRestaurant(neLatitude, neLongitude, swLatitude, swLongitude);

        List<Product> products = Optional.ofNullable(get).orElseThrow(() -> BusinessException.builder().response(HttpResponse.Fail.NOT_FOUND_PRODUCT).build())
                .stream()
                .flatMap(restaurants -> restaurants.getRestaurantStocks()
                        .stream()
                        .map(RestaurantStock::getProduct))
                .toList();

        List<NcpFileResponse> files = products.stream()
                .map(fileService::findAll)
                .filter(Objects::nonNull)
                .toList();

        List<RestaurantLocationResponse> restaurant = get.stream()
                .map(restaurants -> RestaurantLocationResponse.of(restaurants, files))
                .collect(Collectors.toList());

        getRestaurantBusinessStatus(restaurant , LocalTime.now());

        return restaurant;
    }
}
