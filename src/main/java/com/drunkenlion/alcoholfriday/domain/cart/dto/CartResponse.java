package com.drunkenlion.alcoholfriday.domain.cart.dto;

import com.drunkenlion.alcoholfriday.domain.cart.entity.Cart;
import com.drunkenlion.alcoholfriday.domain.cart.entity.CartDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Schema(description = "사용자의 장바구니 내역")
public class CartResponse {
    @Schema(description = "사용자의 장바구니 고유 번호")
    private Long cartId;
    @Schema(description = "사용자의 장바구니 상품(들)")
    private List<CartDetailResponse> cartDetails;
    @Schema(description = "사용자의 장바구니 금액 총합")
    private BigDecimal totalCartPrice;
    @Schema(description = "사용자의 장바구니 개수 총합")
    private Long totalCartQuantity;

    public static CartResponse of(List<CartDetailResponse> cartDetails, Cart cart, List<CartDetail> cartDetailList) {
        return CartResponse.builder()
                .cartId(cart.getId())
                .cartDetails(cartDetails)
                .totalCartPrice(cart.getTotalCartPrice(cartDetailList))
                .totalCartQuantity(cart.getTotalCartQuantity(cartDetailList))
                .build();
    }
}
