package com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.api;

import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.application.RestaurantService;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dto.response.RestaurantDetailProductResponse;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dto.response.RestaurantDetailResponse;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dto.response.RestaurantMapResponse;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dto.response.RestaurantNearbyResponse;
import com.drunkenlion.alcoholfriday.global.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/restaurants")
@Tag(name = "v1-restaurants-controller", description = "매장 관련 API")
public class RestaurantController {
    private final RestaurantService restaurantService;

    @GetMapping
    @Operation(summary = "범위 내 모든 매장 정보 조회")
    public ResponseEntity<List<RestaurantMapResponse>> getRestaurantsWithinBounds(@RequestParam(name = "neLatitude") double neLatitude,
                                                                                  @RequestParam(name = "neLongitude")  double neLongitude,
                                                                                  @RequestParam(name = "swLatitude") double swLatitude,
                                                                                  @RequestParam(name = "swLongitude")  double swLongitude) {
        List<RestaurantMapResponse> responses = restaurantService.findRestaurantInMap(neLatitude, neLongitude, swLatitude, swLongitude);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/nearby")
    @Operation(summary = "상품 주류 취급 매장 조회")
    public ResponseEntity<PageResponse<RestaurantNearbyResponse>> getRestaurantsWithinNearby(@RequestParam(name = "userLocationLatitude") double userLocationLatitude,
                                                                                             @RequestParam(name = "userLocationLongitude")  double userLocationLongitude,
                                                                                             @RequestParam(name = "itemId") Long itemId,
                                                                                             @RequestParam(name = "page", defaultValue = "0") int page,
                                                                                             @RequestParam(name = "size", defaultValue = "5")  int size) {
        PageResponse<RestaurantNearbyResponse> response = PageResponse.of(restaurantService.findRestaurantWithItem(userLocationLatitude, userLocationLongitude, itemId, page, size));
        return ResponseEntity.ok(response);
    }

    @GetMapping("{id}")
    @Operation(summary = "매장 상세 조회")
    public ResponseEntity<RestaurantDetailResponse> getRestaurant(@PathVariable("id") Long restaurantId) {
        RestaurantDetailResponse response = restaurantService.findRestaurant(restaurantId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("{id}/product")
    @Operation(summary = "매장 취급 제품 조회 (paging)")
    public ResponseEntity<PageResponse<RestaurantDetailProductResponse>> getRestaurantStock(@PathVariable("id") Long restaurantId,
                                                                                            @RequestParam(name = "page", defaultValue = "0") int page,
                                                                                            @RequestParam(name = "size", defaultValue = "6")  int size) {
        PageResponse<RestaurantDetailProductResponse> response = PageResponse.of(restaurantService.findRestaurantStock(restaurantId, page, size));
        return ResponseEntity.ok(response);
    }
}
