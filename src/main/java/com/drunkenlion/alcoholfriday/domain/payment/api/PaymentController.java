package com.drunkenlion.alcoholfriday.domain.payment.api;

import com.drunkenlion.alcoholfriday.domain.order.application.OrderService;
import com.drunkenlion.alcoholfriday.domain.order.dto.request.OrderAddressRequest;
import com.drunkenlion.alcoholfriday.global.security.auth.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/payments")
@Tag(name = "v1-payment", description = "결제 관련 API")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {
    private final OrderService orderService;

    @Operation(summary = "결제하기", description = "결제하기 눌렀을 때 주문에 해당 하는 주소 저장")
    @PostMapping("{id}")
    public ResponseEntity<Void> purchase(
            @PathVariable("id") Long orderId,
            @RequestBody OrderAddressRequest orderAddressRequest,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        orderService.updateOrderAddress(orderAddressRequest, orderId, userPrincipal.getMember());
        return ResponseEntity.noContent().build();
    }
}
