package com.drunkenlion.alcoholfriday.domain.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Schema(description = "사용자의 장바구니 내역")
public class CartResponse {
    @Schema(description = "사용자의 장바구니 고유 번호")
    private Long cartId;
    @Schema(description = "사용자의 장바구니 상품들")
    private List<CartDetailResponse> cartDetailResponseList;
}
