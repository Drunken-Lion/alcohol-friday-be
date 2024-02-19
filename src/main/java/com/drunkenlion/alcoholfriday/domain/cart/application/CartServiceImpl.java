package com.drunkenlion.alcoholfriday.domain.cart.application;

import com.drunkenlion.alcoholfriday.domain.cart.dao.CartDetailRepository;
import com.drunkenlion.alcoholfriday.domain.cart.dao.CartRepository;
import com.drunkenlion.alcoholfriday.domain.cart.dto.CartDetailResponse;
import com.drunkenlion.alcoholfriday.domain.cart.dto.CartRequest;
import com.drunkenlion.alcoholfriday.domain.cart.dto.CartResponse;
import com.drunkenlion.alcoholfriday.domain.cart.entity.Cart;
import com.drunkenlion.alcoholfriday.domain.cart.entity.CartDetail;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemRepository;
import com.drunkenlion.alcoholfriday.domain.item.dto.FindItemResponse;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
        Cart cart = addFirstCart(member).orElseGet(() -> cartRepository.save(Cart.create(member)));

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
        Item item = itemRepository.findById(addCart.getItemId()).orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_ITEM).build());

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
    public CartDetailResponse modifyCartItemQuantity(CartRequest modifyCart, Member member) {
        Cart cart = addFirstCart(member).orElseThrow(() -> BusinessException.builder()
                .response(HttpResponse.Fail.NOT_FOUND).build());

        Item item = itemRepository.findById(modifyCart.getItemId()).orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_ITEM).build());

        CartDetail cartDetail = cartDetailRepository.findByItemAndCart(item, cart);

        cartDetail.addQuantity(modifyCart.getQuantity());

        return CartDetailResponse.builder()
                .item(FindItemResponse.of(cartDetail.getItem()))
                .quantity(cartDetail.getQuantity())
                .build();
    }

    @Override
    @Transactional
    public Optional<Cart> addFirstCart(Member member) {
        return cartRepository.findFirstByMember(member);
    }

    // 장바구니 조회
    @Override
    public CartResponse getCartList(Member member) {
        Cart cart = addFirstCart(member).orElseThrow(() -> BusinessException.builder()
                .response(HttpResponse.Fail.NOT_FOUND).build());

        List<CartDetail> cartDetailList = cartDetailRepository.findAllByCart(cart);

        if (cartDetailList.isEmpty()) throw new IllegalArgumentException("현재 장바구니에 상품이 없습니다.");

        List<CartDetailResponse> cartDetails = cartDetailList.stream()
                                                            .map(CartDetailResponse::of)
                                                            .toList();

        return CartResponse.of(cartDetails, cart, cartDetailList);
    }
}
