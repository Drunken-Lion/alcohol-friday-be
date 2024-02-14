package com.drunkenlion.alcoholfriday.domain.admin.restaurant.api;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.application.AdminRestaurantService;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.dto.RestaurantRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.dto.RestaurantDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.dto.RestaurantListResponse;
import com.drunkenlion.alcoholfriday.global.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin")
@Tag(name = "v1-admin-restaurant", description = "관리자 매장관리에 대한 API")
public class AdminRestaurantController {
    private final AdminRestaurantService adminRestaurantService;

    @Operation(summary = "전체 매장 조회", description = "관리자 권한에 대한 전체 매장 조회")
    @GetMapping(value = "restaurants")
    public ResponseEntity<PageResponse<RestaurantListResponse>> getRestaurants(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        PageResponse<RestaurantListResponse> pageResponse = PageResponse.of(this.adminRestaurantService.getRestaurants(page, size));
        return ResponseEntity.ok().body(pageResponse);
    }

    @Operation(summary = "매장 상세 조회", description = "관리자 권한에 대한 매장 상세 조회")
    @GetMapping(value = "restaurants/{id}")
    public ResponseEntity<RestaurantDetailResponse> getRestaurant(
            @PathVariable("id") Long id
    ) {
        RestaurantDetailResponse restaurantDetailResponse = adminRestaurantService.getRestaurant(id);
        return ResponseEntity.ok().body(restaurantDetailResponse);
    }

    @Operation(summary = "매장 등록", description = "관리자 권한에 대한 매장 등록")
    @PostMapping(value = "restaurants")
    public ResponseEntity<RestaurantDetailResponse> createRestaurant(
            @Valid @RequestBody RestaurantRequest restaurantRequest
    ) {
        RestaurantDetailResponse restaurantDetailResponse = adminRestaurantService.createRestaurant(restaurantRequest);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(restaurantDetailResponse.getId())
                .toUri();

        return ResponseEntity.created(location).body(restaurantDetailResponse);
    }

    @Operation(summary = "매장 수정", description = "관리자 권한에 대한 매장 수정")
    @PutMapping(value = "restaurants/{id}")
    public ResponseEntity<RestaurantDetailResponse> modifyRestaurant(
            @PathVariable("id") Long id,
            @Valid @RequestBody RestaurantRequest restaurantRequest
    ) {
        RestaurantDetailResponse restaurantDetailResponse = adminRestaurantService.modifyRestaurant(id, restaurantRequest);
        return ResponseEntity.ok().body(restaurantDetailResponse);
    }
}
