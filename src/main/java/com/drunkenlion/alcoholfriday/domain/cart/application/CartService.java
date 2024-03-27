package com.drunkenlion.alcoholfriday.domain.cart.application;

import com.drunkenlion.alcoholfriday.domain.cart.dto.response.CartDetailResponse;
import com.drunkenlion.alcoholfriday.domain.cart.dto.request.CartRequest;
import com.drunkenlion.alcoholfriday.domain.cart.dto.response.CartResponse;
import com.drunkenlion.alcoholfriday.domain.cart.dto.request.DeleteCartRequest;
import com.drunkenlion.alcoholfriday.domain.cart.entity.Cart;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;

import java.util.List;
import java.util.Optional;

public interface CartService {
    CartResponse addCartList(List<CartRequest> cartRequestList, Member member);
    CartDetailResponse addCart(CartRequest addCart, Cart cart);
    CartDetailResponse modifyCartItemQuantity(CartRequest modifyCart, Member member);
    Optional<Cart> addFirstCart(Member member);
    CartResponse getCartList(Member member);
    void deleteCartList(List<DeleteCartRequest> cartRequest, Member member);
    void deleteCart(DeleteCartRequest cartRequest, Cart cart);
    Cart getCart(Member member);
}
