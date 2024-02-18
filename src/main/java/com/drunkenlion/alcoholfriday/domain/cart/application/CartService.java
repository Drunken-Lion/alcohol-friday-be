package com.drunkenlion.alcoholfriday.domain.cart.application;

import com.drunkenlion.alcoholfriday.domain.cart.dto.CartDetailResponse;
import com.drunkenlion.alcoholfriday.domain.cart.dto.CartRequest;
import com.drunkenlion.alcoholfriday.domain.cart.dto.CartResponse;
import com.drunkenlion.alcoholfriday.domain.cart.entity.Cart;
import com.drunkenlion.alcoholfriday.domain.cart.entity.CartDetail;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;

import java.util.List;

public interface CartService {
    CartResponse addCartList(List<CartRequest> cartRequestList, Member member);
    CartDetailResponse addCart(CartRequest addCart, Cart cart);
    CartDetail modifyCartItemQuantity(CartRequest modifyCart, Member member);
    Cart addFirstCart(Member member);
}
