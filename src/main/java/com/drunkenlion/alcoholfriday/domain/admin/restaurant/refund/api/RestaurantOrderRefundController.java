package com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.api;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.application.RestaurantOrderRefundService;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dto.request.RestaurantOrderRefundCreateRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dto.request.RestaurantOrderRefundRejectRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dto.response.RestaurantOrderRefundResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dto.response.RestaurantOrderRefundResultResponse;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.global.common.response.PageResponse;
import com.drunkenlion.alcoholfriday.global.common.util.RoleValidator;
import com.drunkenlion.alcoholfriday.global.security.auth.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "restaurantId") Long restaurantId
    ) {
        RoleValidator.validateRole(userPrincipal.getMember(), MemberRole.OWNER);

        PageResponse<RestaurantOrderRefundResponse> pageResponse = PageResponse.of(
                this.restaurantOrderRefundService.getRestaurantOrderRefunds(userPrincipal.getMember(), restaurantId, page, size)
        );
        return ResponseEntity.ok().body(pageResponse);
    }

    @Operation(summary = "매장 환불 추가(사장)", description = "사장 권한에 대한 매장 환불 추가")
    @PostMapping("/owner")
    public ResponseEntity<RestaurantOrderRefundResponse> createRestaurantOrderRefund(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody RestaurantOrderRefundCreateRequest request
    ) {
        RoleValidator.validateRole(userPrincipal.getMember(), MemberRole.OWNER);

        RestaurantOrderRefundResponse restaurantOrderRefundResponse
                = restaurantOrderRefundService.createRestaurantOrderRefund(userPrincipal.getMember(), request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(restaurantOrderRefundResponse.getOrderId())
                .toUri();

        return ResponseEntity.created(location).body(restaurantOrderRefundResponse);
    }

    @Operation(summary = "매장 환불 취소(사장)", description = "사장 권한에 대한 매장 환불 취소")
    @PutMapping("/{id}/cancel/owner")
    public ResponseEntity<RestaurantOrderRefundResultResponse> cancelRestaurantOrderRefund(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable("id") Long id
    ) {
        RoleValidator.validateRole(userPrincipal.getMember(), MemberRole.OWNER);

        RestaurantOrderRefundResultResponse response
                = restaurantOrderRefundService.cancelRestaurantOrderRefund(userPrincipal.getMember(), id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "전체 매장 환불 조회(관리자)", description = "관리자 권한에 대한 전체 매장 환불 조회")
    @GetMapping
    public ResponseEntity<PageResponse<RestaurantOrderRefundResponse>> getAdminRestaurantOrderRefunds(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        RoleValidator.validateAdminOrStoreManager(userPrincipal.getMember());

        PageResponse<RestaurantOrderRefundResponse> pageResponse = PageResponse.of(
                this.restaurantOrderRefundService.getAllRestaurantOrderRefunds(page, size)
        );
        return ResponseEntity.ok().body(pageResponse);
    }

    @Operation(summary = "매장 환불 승인(관리자)", description = "관리자 권한에 대한 매장 환불 승인")
    @PutMapping("/{id}")
    public ResponseEntity<RestaurantOrderRefundResultResponse> approvalRestaurantOrderRefund(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable("id") Long id
    ) {
        RoleValidator.validateAdminOrStoreManager(userPrincipal.getMember());

        RestaurantOrderRefundResultResponse response
                = restaurantOrderRefundService.approvalRestaurantOrderRefund(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "매장 환불 반려(관리자)", description = "관리자 권한에 대한 매장 환불 반려")
    @PutMapping("/{id}/reject")
    public ResponseEntity<RestaurantOrderRefundResultResponse> rejectRestaurantOrderRefund(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable("id") Long id,
            @Valid @RequestBody RestaurantOrderRefundRejectRequest request
    ) {
        RoleValidator.validateAdminOrStoreManager(userPrincipal.getMember());

        RestaurantOrderRefundResultResponse response
                = restaurantOrderRefundService.rejectRestaurantOrderRefund(id, request);
        return ResponseEntity.ok(response);
    }
}
