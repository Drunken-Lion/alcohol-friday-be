package com.drunkenlion.alcoholfriday.domain.cart.application;

import com.drunkenlion.alcoholfriday.domain.cart.dto.CartResponse;
import com.drunkenlion.alcoholfriday.domain.cart.entity.Cart;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;

public interface CartService {
    CartResponse getCartList(Member member);

    Cart addFirstCart(Member member);
}
