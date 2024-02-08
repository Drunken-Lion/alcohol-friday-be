package com.drunkenlion.alcoholfriday.domain.cart.application;

import com.drunkenlion.alcoholfriday.domain.cart.dao.CartDetailRepository;
import com.drunkenlion.alcoholfriday.domain.cart.dao.CartRepository;
import com.drunkenlion.alcoholfriday.domain.cart.dto.AddCartRequest;
import com.drunkenlion.alcoholfriday.domain.cart.dto.CartDetailResponse;
import com.drunkenlion.alcoholfriday.domain.cart.entity.Cart;
import com.drunkenlion.alcoholfriday.domain.cart.entity.CartDetail;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemRepository;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
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
    private final ItemRepository itemRepository;

    // 장바구니 추가
    @Override
    public List<CartDetailResponse> addCartList(List<AddCartRequest> cartRequests, Member member) {
        // Cart에 이미 사용자가 있다면 바로 CartDetail로 넘어가도 되겠다.
        // 멤버가 첫 장바구니를 사용할 때
        Cart cart = addFirstCart(member) == null ? Cart.create(member) : addFirstCart(member);

        return cartRequests.stream()
                .map(cartRequest -> addCart(cartRequest, cart))
                .collect(Collectors.toList());
    }

    @Override
    public CartDetailResponse addCart(AddCartRequest addCart, Cart cart) {
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

    // 멤버가 장바구니 이용 경험 있는지 확인
    @Override
    public Cart addFirstCart(Member member) {
        return cartRepository.findFirstByMember(member).orElse(null);
    }
}
