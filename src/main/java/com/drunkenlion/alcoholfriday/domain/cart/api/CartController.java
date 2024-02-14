package com.drunkenlion.alcoholfriday.domain.cart.api;

import com.drunkenlion.alcoholfriday.domain.cart.application.CartService;
import com.drunkenlion.alcoholfriday.domain.cart.dto.CartResponse;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/cart")
@Tag(name = "v1-cart-cartDetail-controller", description = "장바구니 컨트롤러")
public class CartController {
    private final CartService cartService;
    private final MemberService memberService;

    @Operation(summary = "장바구니 조회")
    @GetMapping("list")
//    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartResponse> getCartList(Principal principal) {
        Member member = memberService.findByName(principal.getName());

        CartResponse cartList = cartService.getCartList(member);

        return ResponseEntity.ok().body(cartList);
    }
}
