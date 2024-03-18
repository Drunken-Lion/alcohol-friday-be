package com.drunkenlion.alcoholfriday.domain.restaurant.order.api;

import com.drunkenlion.alcoholfriday.domain.restaurant.order.application.RestaurantOrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/restaurant-orders")
@Tag(name = "v1-restaurant-orders", description = "레스토랑 발주 관련 API")
@SecurityRequirement(name = "bearerAuth")
public class RestaurantOrderController {
    private final RestaurantOrderService restaurantOrderService;
}
