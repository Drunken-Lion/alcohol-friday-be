package com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.api;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.application.RestaurantOrderService;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.response.OwnerRestaurantOrderListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.util.RestaurantOrderOwnerValidator;
import com.drunkenlion.alcoholfriday.global.common.response.PageResponse;
import com.drunkenlion.alcoholfriday.global.security.auth.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin")
@Tag(name = "v1-restaurant-orders", description = "레스토랑 발주 관련 API")
@SecurityRequirement(name = "bearerAuth")
public class RestaurantOrderController {
    private final RestaurantOrderService restaurantOrderService;

    @Operation(summary = "발주 내역 조회 (사업자)", description = "해당 사업자의 모든 발주 내역조회")
    @GetMapping("restaurant-orders/owner")
    public ResponseEntity<PageResponse<OwnerRestaurantOrderListResponse>> getRestaurantOrdersByOwner(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        RestaurantOrderOwnerValidator.validateOwner(userPrincipal.getMember());

        Page<OwnerRestaurantOrderListResponse> pages =
                restaurantOrderService.getRestaurantOrdersByOwner(userPrincipal.getMember(), page, size);

        PageResponse<OwnerRestaurantOrderListResponse> pageResponse = PageResponse.of(pages);

        return ResponseEntity.ok().body(pageResponse);
    }
}
