package com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.application;

import com.drunkenlion.alcoholfriday.domain.item.dao.ItemRepository;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dao.RestaurantRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dao.RestaurantStockRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dto.response.RestaurantDetailProductResponse;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dto.response.RestaurantDetailResponse;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dto.response.RestaurantMapResponse;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dto.response.RestaurantNearbyResponse;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dto.response.RestaurantSimpleProductResponse;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.RestaurantStock;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse.Fail;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import com.drunkenlion.alcoholfriday.global.file.application.FileService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {
    private final ItemRepository itemRepository;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantStockRepository restaurantStockRepository;
    private final FileService fileService;

    @Override
    public List<RestaurantMapResponse> findRestaurantInMap(double neLatitude,
                                                           double neLongitude,
                                                           double swLatitude,
                                                           double swLongitude) {
        List<Restaurant> restaurants = restaurantRepository.getRestaurant(neLatitude, neLongitude, swLatitude, swLongitude);
        List<RestaurantMapResponse> restaurantMapResponses = new ArrayList<>();

        for (Restaurant restaurant : restaurants) {
            List<RestaurantSimpleProductResponse> productResponses = restaurant.getRestaurantStocks().stream()
                    .map(restaurantStock -> RestaurantSimpleProductResponse.of(restaurantStock, fileService.findOne(restaurantStock.getProduct()))).toList();
            restaurantMapResponses.add(RestaurantMapResponse.of(restaurant, productResponses));
        }

        return restaurantMapResponses;
    }

    @Override
    public Page<RestaurantNearbyResponse> findRestaurantWithItem(double userLocationLatitude,
                                                                 double userLocationLongitude,
                                                                 Long itemId, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new BusinessException(Fail.NOT_FOUND_ITEM));
        return restaurantRepository.getRestaurantSellingProducts(userLocationLatitude, userLocationLongitude, item, pageable);
    }

    @Override
    public RestaurantDetailResponse findRestaurant(Long id) {
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new BusinessException(Fail.NOT_FOUND_RESTAURANT));
        return RestaurantDetailResponse.of(restaurant);
    }

    @Override
    public Page<RestaurantDetailProductResponse> findRestaurantStock(Long restaurantId, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<RestaurantStock> restaurantStocks= restaurantStockRepository.findRestaurantStock(restaurantId, pageable);
        return restaurantStocks.map(restaurantStock -> RestaurantDetailProductResponse.of(restaurantStock, fileService.findOne(restaurantStock.getProduct())));
    }
}
