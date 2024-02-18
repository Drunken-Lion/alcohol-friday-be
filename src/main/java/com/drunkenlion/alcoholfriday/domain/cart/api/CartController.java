package com.drunkenlion.alcoholfriday.domain.cart.api;

import com.drunkenlion.alcoholfriday.domain.cart.application.CartService;
import com.drunkenlion.alcoholfriday.domain.cart.dto.CartReqList;
import com.drunkenlion.alcoholfriday.domain.cart.dto.CartResponse;
import com.drunkenlion.alcoholfriday.global.security.auth.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/v1/carts")
public class CartController {
    private final CartService cartService;

    @Operation(summary = "장바구니 상품 등록 API")
    @PostMapping
    public ResponseEntity<CartResponse> addCartList(@RequestBody CartReqList cartReqList, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        CartResponse cartResponse = cartService.addCartList(cartReqList.getCartRequestList(), userPrincipal.getMember());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(cartResponse.getCartId())
                .toUri();

        return ResponseEntity.created(location).body(cartResponse);
    }
}
