package com.drunkenlion.alcoholfriday.domain.cart.application;

import com.drunkenlion.alcoholfriday.domain.cart.dao.CartDetailRepository;
import com.drunkenlion.alcoholfriday.domain.cart.dao.CartRepository;
import com.drunkenlion.alcoholfriday.domain.cart.dto.CartDetailResponse;
import com.drunkenlion.alcoholfriday.domain.cart.entity.Cart;
import com.drunkenlion.alcoholfriday.domain.cart.entity.CartDetail;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;

    // 장바구니 조회
    @Override
    public List<CartDetailResponse> getCartList(Member member) {
        // 멤버 카트 찾기
        Cart cart = cartRepository.findByMember(member)
                .orElseThrow(() -> new IllegalArgumentException("장바구니에 담은 술이 없습니다."));

        // 멤버의 장바구니 내역 가져오기
        List<CartDetail> cartDetailList = cartDetailRepository.findAllByCart(cart);

        // 클라이언트에게 보내기
        return cartDetailList.stream()
                .map(cartDetail -> CartDetailResponse.of(cartDetail, cart))
                .collect(Collectors.toList());
    }
}