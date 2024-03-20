package com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.api;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.application.RestaurantOrderRefundService;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dto.RestaurantInfoRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dto.RestaurantOrderRefundCreateRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dto.RestaurantOrderRefundResponse;
import com.drunkenlion.alcoholfriday.global.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/restaurant-order-refunds")
@Tag(name = "v1-admin-restaurant-order-refund", description = "관리자 매장 재고 환불 관리 API")
@SecurityRequirement(name = "bearerAuth")
public class RestaurantOrderRefundController {
    private final RestaurantOrderRefundService restaurantOrderRefundService;

    @Operation(summary = "매장 별 전체 환불 조회(사장)", description = "사장 권한에 대한 매장 별 전체 환불 조회")
    @GetMapping("/owner")
    public ResponseEntity<PageResponse<RestaurantOrderRefundResponse>> getRestaurantOrderRefunds(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @Valid @RequestBody RestaurantInfoRequest request
    ) {
        PageResponse<RestaurantOrderRefundResponse> pageResponse = PageResponse.of(
                this.restaurantOrderRefundService.getRestaurantOrderRefunds(request, page, size)
        );
        return ResponseEntity.ok().body(pageResponse);
    }

    @Operation(summary = "매장 환불 추가(사장)", description = "사장 권한에 대한 매장 환불 추가")
    @PostMapping("/owner")
    public ResponseEntity<RestaurantOrderRefundResponse> createRestaurantOrderRefund(
            @Valid @RequestBody RestaurantOrderRefundCreateRequest request
    ) {
        RestaurantOrderRefundResponse restaurantOrderRefundResponse = restaurantOrderRefundService.createRestaurantOrderRefund(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(restaurantOrderRefundResponse.getOrderId())
                .toUri();

        return ResponseEntity.created(location).body(restaurantOrderRefundResponse);
    }
}
