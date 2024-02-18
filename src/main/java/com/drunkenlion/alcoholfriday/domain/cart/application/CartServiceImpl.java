package com.drunkenlion.alcoholfriday.domain.cart.application;

import com.drunkenlion.alcoholfriday.domain.cart.dao.CartDetailRepository;
import com.drunkenlion.alcoholfriday.domain.cart.dao.CartRepository;
import com.drunkenlion.alcoholfriday.domain.cart.dto.CartDetailResponse;
import com.drunkenlion.alcoholfriday.domain.cart.dto.CartResponse;
import com.drunkenlion.alcoholfriday.domain.cart.entity.Cart;
import com.drunkenlion.alcoholfriday.domain.cart.entity.CartDetail;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;

    // 장바구니 조회
    @Override
    public CartResponse getCartList(Member member) {
        Cart cart = addFirstCart(member);

        if (cart== null) return getEmptyCart(cart);

        List<CartDetail> cartDetailList = cartDetailRepository.findAllByCart(cart);

        if (cartDetailList.isEmpty()) return getEmptyCart(cart);

        List<CartDetailResponse> cartDetails = cartDetailList.stream()
                                                            .map(CartDetailResponse::of)
                                                            .toList();

        return CartResponse.of(cartDetails, cart, cartDetailList);
    }

    private static CartResponse getEmptyCart(Cart cart) {
        return CartResponse.builder()
                // TODO cartId null을 보내도 될지 물어보기
                .cartId(cart == null ? -1 : cart.getId())
                .cartDetails(Collections.EMPTY_LIST)
                .totalCartPrice(BigDecimal.ZERO)
                .totalCartQuantity(0L)
                .build();
    }

    @Override
    public Cart addFirstCart(Member member) {
        return cartRepository.findFirstByMember(member).orElse(null);
    }
}