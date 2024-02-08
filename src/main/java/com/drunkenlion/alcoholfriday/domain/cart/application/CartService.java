package com.drunkenlion.alcoholfriday.domain.cart.application;

import com.drunkenlion.alcoholfriday.domain.cart.dto.AddCartRequest;
import com.drunkenlion.alcoholfriday.domain.cart.dto.CartDetailResponse;
import com.drunkenlion.alcoholfriday.domain.cart.entity.Cart;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;

import java.util.List;

public interface CartService {
    CartDetailResponse addCart(AddCartRequest addCart, Cart cart);
    List<CartDetailResponse> addCartList(List<AddCartRequest> cartRequests, Member member);
    Cart addFirstCart(Member member);
}
