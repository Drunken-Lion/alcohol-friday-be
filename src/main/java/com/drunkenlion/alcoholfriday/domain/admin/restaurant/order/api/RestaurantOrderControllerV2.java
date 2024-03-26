package com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.api;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.application.RestaurantOrderServiceImplV2;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.request.RestaurantOrderSaveCodeRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.request.RestaurantOrderSaveRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.response.RestaurantOrderResultResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.response.RestaurantOrderSaveCodeResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.response.RestaurantOrderSaveResponse;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.global.common.util.RoleValidator;
import com.drunkenlion.alcoholfriday.global.security.auth.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "v1-admin-restaurant-order", description = "레스토랑 발주 관리 API")
@RequestMapping("/v1/admin")
@RestController
public class RestaurantOrderControllerV2 {
    //TODO
    // - 레스토랑 관리자 기능 전체 구현 후 패키지 구조 및 Controller, Service 병합 예정

    private final RestaurantOrderServiceImplV2 restaurantOrderService;

    @PostMapping("restaurant-orders/owner")
    @Operation(summary = "발주에 필요한 ID 요청 (Owner)", description = "제품 발주 요청 시 필요한 ID 값에 요청 및 장바구니 데이터 저장")
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
    @Operation(summary = "발주 등록 (Owner)", description = "제품 발주 요청 사항 저장 및 작성 완료 처리")
    public ResponseEntity<RestaurantOrderSaveResponse> saveRestaurantOrder(@PathVariable("id") Long restaurantOrderId,
                                                                           @RequestBody RestaurantOrderSaveRequest request,
                                                                           @AuthenticationPrincipal UserPrincipal user) {
        RestaurantOrderSaveResponse response = restaurantOrderService.updateRestaurantOrder(restaurantOrderId, request,
                user.getMember());
        return ResponseEntity.ok(response);
    }

    @PutMapping("restaurant-orders/{id}")
    @Operation(summary = "관리자 발주 승인 처리 (Admin)")
    public ResponseEntity<RestaurantOrderResultResponse> adminOrderApproval(@PathVariable("id") Long restaurantOrderId,
                                                                            @AuthenticationPrincipal UserPrincipal user) {
        RestaurantOrderResultResponse response = restaurantOrderService.adminOrderApproval(restaurantOrderId, user.getMember());
        return ResponseEntity.ok(response);
    }

    @PutMapping("restaurant-orders/{id}/reject")
    @Operation(summary = "관리자 발주 반려 처리 (Admin)")
    public ResponseEntity<RestaurantOrderResultResponse> adminOrderRejectedApproval(@PathVariable("id") Long restaurantOrderId,
                                                                                    @AuthenticationPrincipal UserPrincipal user) {
        RestaurantOrderResultResponse response = restaurantOrderService.adminOrderRejectedApproval(restaurantOrderId, user.getMember());
        return ResponseEntity.ok(response);
    }

    @PutMapping("restaurant-orders/{id}/cancel/owner")
    @Operation(summary = "사장 발주 취소 처리 (Owner)")
    public ResponseEntity<RestaurantOrderResultResponse> ownerOrderCancel(@PathVariable("id") Long restaurantOrderId,
                                                                          @AuthenticationPrincipal UserPrincipal user) {
        RoleValidator.validateRole(user.getMember(), MemberRole.OWNER);

        RestaurantOrderResultResponse response = restaurantOrderService.ownerOrderCancel(restaurantOrderId, user.getMember());
        return ResponseEntity.ok(response);
    }
}
