package com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.api;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.application.AdminRestaurantStockService;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.dto.request.RestaurantStockModifyRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.dto.response.RestaurantStockListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.dto.response.RestaurantStockModifyResponse;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/restaurants/{restaurantId}/stocks")
@Tag(name = "v1-admin-restaurant-{restaurantId}-stocks", description = "관리자 매장 재고 관리 API")
@SecurityRequirement(name = "bearerAuth")
public class AdminRestaurantStockController {
    private final AdminRestaurantStockService adminRestaurantStockService;

    @Operation(summary = "매장 재고 조회", description = "해당 매장의 재고를 조회")
    @GetMapping
    public ResponseEntity<PageResponse<RestaurantStockListResponse>> getRestaurantStocks(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable("restaurantId") Long restaurantId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "12") int size) {

        RoleValidator.validateAdminOrStoreManagerOrOwner(userPrincipal.getMember());

        Page<RestaurantStockListResponse> pages =
                adminRestaurantStockService.getRestaurantStocks(userPrincipal.getMember(), restaurantId, page, size);

        PageResponse<RestaurantStockListResponse> pageResponse = PageResponse.of(pages);

        return ResponseEntity.ok().body(pageResponse);
    }

    @Operation(summary = "매장 재고 수정", description = "해당 매장의 재고를 수정")
    @PutMapping
    public ResponseEntity<RestaurantStockModifyResponse> modifyRestaurantStock(
            @PathVariable("restaurantId") Long restaurantId,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody RestaurantStockModifyRequest modifyRequest) {

        RoleValidator.validateAdminOrStoreManagerOrOwner(userPrincipal.getMember());

        RestaurantStockModifyResponse modifyResponse =
                adminRestaurantStockService.modifyRestaurantStock(restaurantId, userPrincipal.getMember(), modifyRequest);

        return ResponseEntity.ok().body(modifyResponse);
    }
}
