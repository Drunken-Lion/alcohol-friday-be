package com.drunkenlion.alcoholfriday.domain.order.api;

import com.drunkenlion.alcoholfriday.domain.order.application.OrderService;
import com.drunkenlion.alcoholfriday.domain.order.dto.request.OrderRequestList;
import com.drunkenlion.alcoholfriday.domain.order.dto.response.OrderResponseList;
import com.drunkenlion.alcoholfriday.global.security.auth.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/orders")
@Tag(name = "v1-order", description = "주문 관련 API")
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "주문 접수", description = "결제 페이지에서 결제하기 눌렀을 때 (고객의 배송 정보가 다 입력된 상태)")
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
}
