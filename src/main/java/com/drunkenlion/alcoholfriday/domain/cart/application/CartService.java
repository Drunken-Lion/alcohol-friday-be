package com.drunkenlion.alcoholfriday.domain.cart.application;

import com.drunkenlion.alcoholfriday.domain.cart.dto.CartDetailResponse;
import com.drunkenlion.alcoholfriday.domain.cart.dto.CartRequest;
import com.drunkenlion.alcoholfriday.domain.cart.dto.CartResponse;
import com.drunkenlion.alcoholfriday.domain.cart.entity.Cart;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;

import java.util.List;
import java.util.Optional;

public interface CartService {
    CartResponse addCartList(List<CartRequest> cartRequestList, Member member);
    CartDetailResponse addCart(CartRequest addCart, Cart cart);
    CartDetailResponse modifyCartItemQuantity(CartRequest modifyCart, Member member);
    Optional<Cart> addFirstCart(Member member);
    List<CartDetailResponse> getCartList(Member member);
}
