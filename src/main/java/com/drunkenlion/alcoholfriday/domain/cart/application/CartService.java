package com.drunkenlion.alcoholfriday.domain.cart.application;

import com.drunkenlion.alcoholfriday.domain.cart.dto.CartDetailResponse;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;

import java.util.List;

public interface CartService {
    List<CartDetailResponse> getCartList(Member member);
}
