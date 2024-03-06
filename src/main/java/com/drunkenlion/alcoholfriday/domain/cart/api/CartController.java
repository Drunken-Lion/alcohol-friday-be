package com.drunkenlion.alcoholfriday.domain.cart.api;

import com.drunkenlion.alcoholfriday.domain.cart.application.CartService;
import com.drunkenlion.alcoholfriday.domain.cart.dto.request.CartDeleteReqList;
import com.drunkenlion.alcoholfriday.domain.cart.dto.request.CartReqList;
import com.drunkenlion.alcoholfriday.domain.cart.dto.request.CartRequest;
import com.drunkenlion.alcoholfriday.domain.cart.dto.response.CartDetailResponse;
import com.drunkenlion.alcoholfriday.domain.cart.dto.response.CartResponse;
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
@RequestMapping("/v1/carts")
@Tag(name = "v1-cart-cartDetail", description = "장바구니 관련 API")
@SecurityRequirement(name = "bearerAuth")
public class CartController {
    private final CartService cartService;

    @Operation(summary = "장바구니에 상품(들) 추가")
    @PostMapping
    public ResponseEntity<CartResponse> addCartList(
            @RequestBody CartReqList cartReqList,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        CartResponse cartResponse = cartService.addCartList(cartReqList.getCartRequestList(), userPrincipal.getMember());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(cartResponse.getCartId())
                .toUri();

        return ResponseEntity.created(location).body(cartResponse);
    }

    @Operation(summary = "장바구니 상품 수량 변경")
    @PutMapping
    public ResponseEntity<CartDetailResponse> modifyCart(
            @RequestBody CartRequest modifyCartRequest,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        CartDetailResponse cartDetailResponse = cartService.modifyCartItemQuantity(modifyCartRequest, userPrincipal.getMember());

        return ResponseEntity.ok().body(cartDetailResponse);
    }

    @Operation(summary = "장바구니 조회")
    @GetMapping
    public ResponseEntity<CartResponse> getCartList(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        CartResponse cartList = cartService.getCartList(userPrincipal.getMember());

        return ResponseEntity.ok().body(cartList);
    }

    @Operation(summary = "장바구니 내역 삭제")
    @DeleteMapping
    public ResponseEntity<Void> deleteCartList(@RequestBody CartDeleteReqList deleteCartRequest, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        cartService.deleteCartList(deleteCartRequest.getCartDeleteReqList(), userPrincipal.getMember());

        return ResponseEntity.noContent().build();
    }
}
