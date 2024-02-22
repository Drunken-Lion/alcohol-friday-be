package com.drunkenlion.alcoholfriday.domain.cart.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Schema(description = "장바구니에 추가할 상품")
public class CartRequest {
    @Schema(description = "장바구니에 추가할 상품 고유 번호")
    private Long itemId;
    @Schema(description = "장바구니에 추가할 상품의 수량")
    private Long quantity;
}
