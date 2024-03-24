package com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.api;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.application.RestaurantOrderCartService;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dto.request.RestaurantOrderCartSaveRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dto.response.RestaurantOrderCartSaveResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dto.response.RestaurantOrderProductListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dto.request.RestaurantOrderCartDeleteRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dto.request.RestaurantOrderCartUpdateRequest;
import com.drunkenlion.alcoholfriday.global.common.response.PageResponse;
import com.drunkenlion.alcoholfriday.global.security.auth.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "v1-admin-restaurant-orders-carts", description = "레스토랑 발주 장바구니 관리 API")
@RequestMapping("/v1/admin/restaurant-orders-carts")
@RestController
public class RestaurantOrderCartController {
    private final RestaurantOrderCartService restaurantOrderCartService;

    @GetMapping("owner")
    @Operation(summary = "제품 목록 (Owner)", description = "발주를 위한 제품 목록")
    public ResponseEntity<PageResponse<RestaurantOrderProductListResponse>> getRestaurantOrderProducts(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal user) {
        PageResponse<RestaurantOrderProductListResponse> response = PageResponse.of(
                this.restaurantOrderCartService.getRestaurantOrderProducts(page, size, user.getMember()));
        return ResponseEntity.ok(response);
    }

    @PostMapping("owner")
    @Operation(summary = "장바구니 추가 (Owner)", description = "제품 발주 장바구니 추가")
    public ResponseEntity<RestaurantOrderCartSaveResponse> addOwnerCart(@RequestBody RestaurantOrderCartSaveRequest request,
                                                                        @AuthenticationPrincipal UserPrincipal user) {
        RestaurantOrderCartSaveResponse response = restaurantOrderCartService.saveRestaurantOrderCart(request, user.getMember());
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("{id}/owner")
    @Operation(summary = "장바구니 수량 변경 (Owner)", description = "제품 발주 장바구니 수량 변경")
    public ResponseEntity<RestaurantOrderCartSaveResponse> updateOwnerCart(
            @PathVariable("id") Long restaurantOrderCartId,
            @RequestBody RestaurantOrderCartUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal user) {
        RestaurantOrderCartSaveResponse response = restaurantOrderCartService.updateRestaurantOrderCart(restaurantOrderCartId, request, user.getMember());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("{id}/owner")
    @Operation(summary = "장바구니 삭제 (Owner)", description = "발주 장바구니 제품 삭제")
    public ResponseEntity<RestaurantOrderCartSaveResponse> deleteOwnerCart(
            @PathVariable("id") Long restaurantOrderCartId,
            @RequestBody RestaurantOrderCartDeleteRequest request,
            @AuthenticationPrincipal UserPrincipal user) {
        RestaurantOrderCartSaveResponse response = restaurantOrderCartService.deleteRestaurantOrderCart(restaurantOrderCartId, request, user.getMember());
        return ResponseEntity.ok(response);
    }
}
