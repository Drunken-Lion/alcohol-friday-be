package com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.api;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.application.RestaurantOrderService;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.RestaurantOrderListResponse;
import com.drunkenlion.alcoholfriday.global.common.response.PageResponse;
import com.drunkenlion.alcoholfriday.global.security.auth.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin")
@Tag(name = "v1-restaurant-orders", description = "레스토랑 발주 관련 API")
@SecurityRequirement(name = "bearerAuth")
public class RestaurantOrderController {
    private final RestaurantOrderService restaurantOrderService;

    @Operation(summary = "발주 내역 조회 (사업자)", description = "해당 사업자의 모든 발주 내역조회")
    @GetMapping("restaurant-orders/owner")
    public ResponseEntity<PageResponse<RestaurantOrderListResponse>> getRestaurantOrdersByOwner(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        // 해당 사용자의 모든 발주내역 확인

        Page<RestaurantOrderListResponse> pages =
                restaurantOrderService.getRestaurantOrdersByOwner(userPrincipal.getMember(), page, size);

        PageResponse<RestaurantOrderListResponse> pageResponse = PageResponse.of(pages);

        return ResponseEntity.ok().body(pageResponse);
    }
}
