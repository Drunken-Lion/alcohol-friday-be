package com.drunkenlion.alcoholfriday.domain.cart.application;

import com.drunkenlion.alcoholfriday.domain.cart.dao.CartDetailRepository;
import com.drunkenlion.alcoholfriday.domain.cart.dao.CartRepository;
import com.drunkenlion.alcoholfriday.domain.cart.dto.DeleteCartRequest;
import com.drunkenlion.alcoholfriday.domain.cart.entity.Cart;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;

    @Override
    public void deleteCartList(List<DeleteCartRequest> cartRequests, Member member) {
        Cart cart = addFirstCart(member);

        if (cart == null) throw new IllegalArgumentException("현재 장바구니에 상품이 없습니다.");

        cartRequests.forEach(cartRequest -> deleteCart(cartRequest, cart));
    }

    @Override
    public void deleteCart(DeleteCartRequest cartRequest, Cart cart) {
        cartDetailRepository.deleteByIdAndCart(cartRequest.getItemId(), cart);
    }

    @Override
    public Cart addFirstCart(Member member) {
        return cartRepository.findFirstByMember(member).orElse(null);
    }
}
