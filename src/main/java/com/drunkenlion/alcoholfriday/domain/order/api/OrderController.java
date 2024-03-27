package com.drunkenlion.alcoholfriday.domain.order.api;

import com.drunkenlion.alcoholfriday.domain.order.application.OrderService;
import com.drunkenlion.alcoholfriday.domain.order.dto.OrderResponse;
import com.drunkenlion.alcoholfriday.domain.order.dto.request.OrderAddressRequest;
import com.drunkenlion.alcoholfriday.domain.order.dto.request.OrderCancelRequest;
import com.drunkenlion.alcoholfriday.domain.order.dto.request.OrderRequestList;
import com.drunkenlion.alcoholfriday.domain.order.dto.response.OrderResponseList;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/orders")
@Tag(name = "v1-order", description = "주문 관련 API")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "구매하기 (주문 접수)", description = "즉시 구매 또는 장바구니에서 구매할 때 (결제 페이지 들어가기 전)")
    @PostMapping
    public ResponseEntity<OrderResponseList> receive(
            @RequestBody OrderRequestList orderRequestList,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        OrderResponseList savedOrder = orderService.receive(orderRequestList, userPrincipal.getMember());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedOrder.getId())
                .toUri();

        return ResponseEntity.created(location).body(savedOrder);
    }

    @Operation(summary = "주문 배송지 저장", description = "결제하기 눌렀을 때 주문에 해당 하는 주소 저장 (결제 페이지)")
    @PostMapping("{id}")
    public ResponseEntity<Void> updateOrderAddress(
            @PathVariable("id") Long orderId,
            @RequestBody OrderAddressRequest orderAddressRequest,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        orderService.updateOrderAddress(orderAddressRequest, orderId, userPrincipal.getMember());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "주문 취소", description = "OrderStatus에서 결제 완료, 배송 준비 중일 경우 주문 취소 가능")
    @PutMapping("{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable("id") Long orderId,
            @RequestBody OrderCancelRequest orderCancelRequest,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        OrderResponse orderResponse = orderService.cancelOrder(orderId, orderCancelRequest, userPrincipal.getMember());
        return ResponseEntity.ok().body(orderResponse);
    }
}
