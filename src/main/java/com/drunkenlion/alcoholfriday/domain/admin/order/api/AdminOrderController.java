package com.drunkenlion.alcoholfriday.domain.admin.order.api;

import com.drunkenlion.alcoholfriday.domain.admin.order.application.AdminOrderService;
import com.drunkenlion.alcoholfriday.domain.admin.order.dto.OrderDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.order.dto.OrderListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.order.dto.OrderModifyRequest;
import com.drunkenlion.alcoholfriday.global.common.enumerated.OrderStatus;
import com.drunkenlion.alcoholfriday.global.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/orders")
@Tag(name = "v1-admin-order", description = "관리자 주문 관리 API")
@SecurityRequirement(name = "bearerAuth")
public class AdminOrderController {
    private final AdminOrderService adminOrderService;

    @Operation(summary = "전체 주문 조회", description = "관리자 권한에 대한 전체 주문 조회")
    @GetMapping
    public ResponseEntity<PageResponse<OrderListResponse>> getOrders(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "status", required = false) OrderStatus status
    ) {
        PageResponse<OrderListResponse> pageResponse = PageResponse.of(this.adminOrderService.getOrdersByOrderStatus(page, size, status));
        return ResponseEntity.ok().body(pageResponse);
    }

    @Operation(summary = "주문 상세 조회", description = "관리자 권한에 대한 주문 상세 조회")
    @GetMapping("{id}")
    public ResponseEntity<OrderDetailResponse> getOrder(
            @PathVariable("id") Long id
    ) {
        OrderDetailResponse orderDetailResponse = adminOrderService.getOrder(id);
        return ResponseEntity.ok().body(orderDetailResponse);
    }

    @Operation(summary = "주문 수정", description = "관리자 권한에 대한 주문 수정")
    @PutMapping("{id}")
    public ResponseEntity<OrderDetailResponse> modifyOrder(
            @PathVariable("id") Long id,
            @Valid @RequestBody OrderModifyRequest orderModifyRequest
    ) {
        OrderDetailResponse orderDetailResponse = adminOrderService.modifyOrder(id, orderModifyRequest);
        return ResponseEntity.ok().body(orderDetailResponse);
    }
}
