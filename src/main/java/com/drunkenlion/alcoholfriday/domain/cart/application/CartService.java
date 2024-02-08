package com.drunkenlion.alcoholfriday.domain.cart.application;

import com.drunkenlion.alcoholfriday.domain.cart.dto.DeleteCartRequest;
import com.drunkenlion.alcoholfriday.domain.cart.entity.Cart;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;

import java.util.List;

public interface CartService {
    void deleteCart(List<DeleteCartRequest> cartRequest, Member member)
    void deleteCart(DeleteCartRequest cartRequest, Cart cart);
    Cart addFirstCart(Member member);
}