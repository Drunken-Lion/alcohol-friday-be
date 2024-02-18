package com.drunkenlion.alcoholfriday.domain.cart.api;

import com.drunkenlion.alcoholfriday.domain.cart.application.CartService;
import com.drunkenlion.alcoholfriday.domain.cart.dto.CartDetailResponse;
import com.drunkenlion.alcoholfriday.domain.cart.dto.CartReqList;
import com.drunkenlion.alcoholfriday.domain.cart.dto.CartRequest;
import com.drunkenlion.alcoholfriday.domain.cart.dto.CartResponse;
import com.drunkenlion.alcoholfriday.global.security.auth.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/carts")
@Tag(name = "v1-cart-cartDetail-controller", description = "장바구니 관련 API")
public class CartController {
    private final CartService cartService;

    @Operation(summary = "장바구니에 상품(들) 추가")
    @PostMapping
    public ResponseEntity<CartResponse> addCartList(@RequestBody CartReqList cartReqList, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        CartResponse cartResponse = cartService.addCartList(cartReqList.getCartRequestList(), userPrincipal.getMember());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(cartResponse.getCartId())
                .toUri();

        return ResponseEntity.created(location).body(cartResponse);
    }

    @Operation(summary = "장바구니 상품 수량 변경")
    @PutMapping
    public ResponseEntity<CartDetailResponse> modifyCart(@RequestBody CartRequest modifyCartRequest, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        CartDetailResponse cartDetailResponse = cartService.modifyCartItemQuantity(modifyCartRequest, userPrincipal.getMember());

        return ResponseEntity.ok().body(cartDetailResponse);
    }
}
