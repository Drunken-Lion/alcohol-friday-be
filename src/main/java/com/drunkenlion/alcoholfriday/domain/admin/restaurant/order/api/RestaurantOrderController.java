package com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.api;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dto.response.RestaurantOrderProductListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.application.RestaurantOrderService;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.response.OwnerRestaurantOrderListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.response.RestaurantOrderListResponse;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.global.common.response.PageResponse;
import com.drunkenlion.alcoholfriday.global.common.util.RoleValidator;
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
@RequestMapping("/v1/admin/restaurant-orders")
@Tag(name = "v1-restaurant-orders", description = "레스토랑 발주 관련 API")
@SecurityRequirement(name = "bearerAuth")
public class RestaurantOrderController {
    private final RestaurantOrderService restaurantOrderService;

    @Operation(summary = "발주 내역 조회 (관리자 or 스토어 관리자)", description = "들어온 모든 발주 내역을 조회")
    @GetMapping
    public ResponseEntity<PageResponse<RestaurantOrderListResponse>> getRestaurantOrdersByAdminOrStoreManager(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        RoleValidator.validateAdminOrStoreManager(userPrincipal.getMember());

        Page<RestaurantOrderListResponse> pages =
                restaurantOrderService.getRestaurantOrdersByAdminOrStoreManager(userPrincipal.getMember(), page, size);

        PageResponse<RestaurantOrderListResponse> pageResponse = PageResponse.of(pages);

        return ResponseEntity.ok().body(pageResponse);
    }

    @Operation(summary = "발주 내역 조회 (사업자)", description = "해당 레스토랑의 발주 내역 조회")
    @GetMapping("owner")
    public ResponseEntity<PageResponse<OwnerRestaurantOrderListResponse>> getRestaurantOrdersByOwner(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(name = "restaurantId") Long restaurantId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        RoleValidator.validateRole(userPrincipal.getMember(), MemberRole.OWNER);

        Page<OwnerRestaurantOrderListResponse> pages =
                restaurantOrderService.getRestaurantOrdersByOwner(userPrincipal.getMember(), restaurantId, page, size);

        PageResponse<OwnerRestaurantOrderListResponse> pageResponse = PageResponse.of(pages);

        return ResponseEntity.ok().body(pageResponse);
    }

    @Operation(summary = "제품 목록 (사업자)", description = "발주를 위한 제품 목록")
    @GetMapping("products")
    public ResponseEntity<PageResponse<RestaurantOrderProductListResponse>> getRestaurantOrderProducts(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal user) {
        PageResponse<RestaurantOrderProductListResponse> response = PageResponse.of(restaurantOrderService.getRestaurantOrderProducts(page, size, user.getMember()));
        return ResponseEntity.ok(response);
    }

}
