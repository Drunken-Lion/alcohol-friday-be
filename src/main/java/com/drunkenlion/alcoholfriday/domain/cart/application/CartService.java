package com.drunkenlion.alcoholfriday.domain.cart.application;

import com.drunkenlion.alcoholfriday.domain.cart.dto.AddCartRequest;
import com.drunkenlion.alcoholfriday.domain.cart.dto.CartDetailResponse;
import com.drunkenlion.alcoholfriday.domain.cart.entity.Cart;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;

public interface CartService {
    CartDetailResponse addCart(AddCartRequest addCart, Member member);
    void modifyCartItemQuantity(AddCartRequest modifyCart, Member member);
    Cart addFirstCart(Member member);
}
