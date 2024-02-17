package com.drunkenlion.alcoholfriday.domain.cart.dto;

import com.drunkenlion.alcoholfriday.domain.cart.entity.CartDetail;
import com.drunkenlion.alcoholfriday.domain.item.dto.FindItemResponse;
import lombok.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class CartDetailResponse {
    private Long cartId;
    private FindItemResponse item;
    private Long quantity;

    public static CartDetailResponse of(CartDetail cartDetail) {
        return CartDetailResponse.builder()
                .item(FindItemResponse.of(cartDetail.getItem()))
                .quantity(cartDetail.getQuantity())
                .build();
    }
}
