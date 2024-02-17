package com.drunkenlion.alcoholfriday.domain.cart.application;

import com.drunkenlion.alcoholfriday.domain.cart.dao.CartDetailRepository;
import com.drunkenlion.alcoholfriday.domain.cart.dao.CartRepository;
import com.drunkenlion.alcoholfriday.domain.cart.dto.CartDetailResponse;
import com.drunkenlion.alcoholfriday.domain.cart.dto.CartRequest;
import com.drunkenlion.alcoholfriday.domain.cart.dto.CartResponse;
import com.drunkenlion.alcoholfriday.domain.cart.entity.Cart;
import com.drunkenlion.alcoholfriday.domain.cart.entity.CartDetail;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemRepository;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
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
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public CartResponse addCartList(List<CartRequest> cartRequestList, Member member) {
        Cart cart = addFirstCart(member) != null ? addFirstCart(member) : cartRepository.save(Cart.create(member));

        List<CartDetailResponse> cartDetailResponseList = cartRequestList.stream()
                .map(cartRequest -> addCart(cartRequest, cart))
                .toList();

        return CartResponse.builder()
                .cartId(cart.getId())
                .cartDetailResponseList(cartDetailResponseList)
                .build();
    }

    @Override
    @Transactional
    public CartDetailResponse addCart(CartRequest addCart, Cart cart) {
        Item item = itemRepository.findById(addCart.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다."));

        CartDetail cartDetail = CartDetail.builder()
                .cart(cart)
                .item(item)
                .quantity(addCart.getQuantity())
                .build();

        cartDetailRepository.save(cartDetail);

        return CartDetailResponse.of(cartDetail);
    }

    // 상품 수량만 변경할 경우
    @Override
    @Transactional
    public CartDetail modifyCartItemQuantity(CartRequest modifyCart, Member member) {
        Cart cart = addFirstCart(member);

        Item item = itemRepository.findById(modifyCart.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다."));

        CartDetail cartDetail = cartDetailRepository.findByItemAndCart(item, cart);

        cartDetail.setQuantity(modifyCart.getQuantity());

        return cartDetail;
    }

    @Override
    @Transactional
    public Cart addFirstCart(Member member) {
        return cartRepository.findFirstByMember(member).orElse(null);
    }
}
