package com.drunkenlion.alcoholfriday.domain.cart.application;

import com.drunkenlion.alcoholfriday.domain.cart.dto.AddCartRequest;
import com.drunkenlion.alcoholfriday.domain.cart.dto.CartDetailResponse;
import com.drunkenlion.alcoholfriday.domain.cart.entity.Cart;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;

import java.util.List;

public interface CartService {
    List<CartDetailResponse> addCartList(List<AddCartRequest> cartRequestList, Member member);
    CartDetailResponse addCart(AddCartRequest addCart, Cart cart);
    void modifyCartItemQuantity(AddCartRequest modifyCart, Member member);
    Cart addFirstCart(Member member);
}
