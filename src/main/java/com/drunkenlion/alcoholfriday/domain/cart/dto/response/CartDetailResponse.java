package com.drunkenlion.alcoholfriday.domain.cart.dto.response;

import com.drunkenlion.alcoholfriday.domain.cart.entity.CartDetail;
import com.drunkenlion.alcoholfriday.domain.item.dto.FindItemResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Schema(description = "사용자의 장바구니 상품 세부사항 항목")
public class CartDetailResponse {
    @Schema(description = "상품 정보")
    private FindItemResponse item;
    @Schema(description = "장바구니의 상품 수량")
    private Long quantity;

    public static CartDetailResponse of(CartDetail cartDetail) {
        return CartDetailResponse.builder()
                .item(FindItemResponse.of(cartDetail.getItem()))
                .quantity(cartDetail.getQuantity())
                .build();
    }
}
