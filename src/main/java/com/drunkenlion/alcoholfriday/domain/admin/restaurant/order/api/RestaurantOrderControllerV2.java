package com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.api;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.application.RestaurantOrderServiceImplV2;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.request.RestaurantOrderSaveCodeRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.request.RestaurantOrderSaveRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.response.RestaurantAdminOrderApprovalResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.response.RestaurantOrderSaveCodeResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.response.RestaurantOrderSaveResponse;
import com.drunkenlion.alcoholfriday.global.security.auth.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "v1-admin-restaurant-order", description = "레스토랑 발주 관리 API")
@RequestMapping("/v1/admin")
@RestController
public class RestaurantOrderControllerV2 {
    private final RestaurantOrderServiceImplV2 restaurantOrderService;

    @PostMapping("restaurant-orders/owner")
    @Operation(summary = "발주에 필요한 ID 요청", description = "제품 발주 요청 시 필요한 ID 값에 요청 및 장바구니 데이터 저장")
    public ResponseEntity<RestaurantOrderSaveCodeResponse> getSaveCode(@RequestBody RestaurantOrderSaveCodeRequest request,
                                                                       @AuthenticationPrincipal UserPrincipal user) {
        RestaurantOrderSaveCodeResponse response = restaurantOrderService.getSaveCode(request, user.getMember());
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("restaurant-orders/{id}/owner")
    public ResponseEntity<RestaurantOrderSaveResponse> saveRestaurantOrder(@PathVariable("id") Long restaurantOrderId,
                                                                           @RequestBody RestaurantOrderSaveRequest request,
                                                                           @AuthenticationPrincipal UserPrincipal user) {
        RestaurantOrderSaveResponse response = restaurantOrderService.updateRestaurantOrder(restaurantOrderId, request,
                user.getMember());
        return ResponseEntity.ok(response);
    }

    @PutMapping("restaurant-orders/{id}")
    public ResponseEntity<RestaurantAdminOrderApprovalResponse> adminOrderApproval(@PathVariable("id") Long restaurantOrderId,
                                                                                   @AuthenticationPrincipal UserPrincipal user) {
        RestaurantAdminOrderApprovalResponse response = restaurantOrderService.adminOrderApproval(restaurantOrderId, user.getMember());
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("restaurant-orders/{id}")
    public ResponseEntity<RestaurantAdminOrderApprovalResponse> adminOrderRejectedApproval(@PathVariable("id") Long restaurantOrderId,
                                                         @AuthenticationPrincipal UserPrincipal user) {
        RestaurantAdminOrderApprovalResponse response = restaurantOrderService.adminOrderRejectedApproval(restaurantOrderId, user.getMember());
        return ResponseEntity.ok(response);
    }
}
