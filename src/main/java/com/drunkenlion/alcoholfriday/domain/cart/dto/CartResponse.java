package com.drunkenlion.alcoholfriday.domain.cart.dto;

import com.drunkenlion.alcoholfriday.domain.cart.entity.Cart;
import com.drunkenlion.alcoholfriday.domain.cart.entity.CartDetail;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class CartResponse {
    private List<CartDetailResponse> cartDetails;
    private BigDecimal totalCartPrice;
    private Long totalCartQuantity;

    public static CartResponse of(List<CartDetailResponse> cartDetails, Cart cart, List<CartDetail> cartDetailList) {
        return CartResponse.builder()
                .cartDetails(cartDetails)
                .totalCartPrice(cart.getTotalCartPrice(cartDetailList))
                .totalCartQuantity(cart.getTotalCartQuantity(cartDetailList))
                .build();
    }
}
