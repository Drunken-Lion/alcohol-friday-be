package com.drunkenlion.alcoholfriday.domain.cart.dto;

import com.drunkenlion.alcoholfriday.domain.cart.entity.Cart;
import com.drunkenlion.alcoholfriday.domain.cart.entity.CartDetail;
import com.drunkenlion.alcoholfriday.domain.item.dto.FindItemResponse;
import lombok.*;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class CartDetailResponse {
    private FindItemResponse item;
    private Long quantity;
    private BigDecimal totalCartPrice;

    public static CartDetailResponse of(CartDetail cartDetail, Cart cart) {
        return CartDetailResponse.builder()
                .item(FindItemResponse.of(cartDetail.getItem()))
                .quantity(cartDetail.getQuantity())
                .totalCartPrice(cart.getTotalCartPrice())
                .build();
    }
}
