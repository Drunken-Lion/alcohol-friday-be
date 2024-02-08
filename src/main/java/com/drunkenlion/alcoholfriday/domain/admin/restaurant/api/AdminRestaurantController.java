package com.drunkenlion.alcoholfriday.domain.admin.restaurant.api;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.application.AdminRestaurantService;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.dto.RestaurantDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.dto.RestaurantListResponse;
import com.drunkenlion.alcoholfriday.global.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin")
@Tag(name = "v1-admin-restaurant-controller", description = "관리자 매장관리 컨트롤러")
public class AdminRestaurantController {
    private final AdminRestaurantService adminRestaurantService;

    @Operation(summary = "전체 매장 조회")
    @GetMapping(value = "restaurants")
    public ResponseEntity<PageResponse<RestaurantListResponse>> getRestaurants(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        PageResponse<RestaurantListResponse> pageResponse = PageResponse.of(this.adminRestaurantService.getRestaurants(page, size));
        return ResponseEntity.ok().body(pageResponse);
    }

    @Operation(summary = "매장 상세 조회")
    @GetMapping(value = "restaurant/{id}")
    public ResponseEntity<RestaurantDetailResponse> getRestaurant(
            @PathVariable("id") Long id
    ) {
        RestaurantDetailResponse restaurantDetailResponse = adminRestaurantService.getRestaurant(id);
        return ResponseEntity.ok().body(restaurantDetailResponse);
    }

}
