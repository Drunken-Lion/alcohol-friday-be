package com.drunkenlion.alcoholfriday.domain.restaurant.api;


import com.drunkenlion.alcoholfriday.domain.restaurant.application.RestaurantService;
import com.drunkenlion.alcoholfriday.domain.restaurant.dto.response.RestaurantLocationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/restaurants")
@Tag(name = "v1-restaurants-controller", description = "레스토랑 관련 컨트롤러")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @Operation(summary = "모든 레스토랑 정보 조회")
    @GetMapping("/nearby-restaurant")
    public ResponseEntity<List<RestaurantLocationResponse>> nearby(
            @RequestParam(name = "neLatitude") double neLatitude,
            @RequestParam(name = "neLongitude")  double neLongitude,
            @RequestParam(name = "swLatitude") double swLatitude,
            @RequestParam(name = "swLongitude")  double swLongitude
    ) {
        List<RestaurantLocationResponse> restaurantSearch = restaurantService.getRestaurants( neLatitude,  neLongitude,  swLatitude,  swLongitude);
        return ResponseEntity.ok().body(restaurantSearch);
    }
}
