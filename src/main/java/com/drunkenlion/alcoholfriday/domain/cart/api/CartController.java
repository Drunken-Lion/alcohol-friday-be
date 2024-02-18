package com.drunkenlion.alcoholfriday.domain.cart.api;

import com.drunkenlion.alcoholfriday.domain.cart.application.CartService;
import com.drunkenlion.alcoholfriday.domain.cart.dto.CartResponse;
import com.drunkenlion.alcoholfriday.global.security.auth.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/carts")
@Tag(name = "v1-cart-cartDetail-controller", description = "장바구니 컨트롤러")
public class CartController {
    private final CartService cartService;

    @Operation(summary = "장바구니 조회 API")
    @GetMapping("list")
    public ResponseEntity<CartResponse> getCartList(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        CartResponse cartList = cartService.getCartList(userPrincipal.getMember());

        return ResponseEntity.ok().body(cartList);
    }
}
