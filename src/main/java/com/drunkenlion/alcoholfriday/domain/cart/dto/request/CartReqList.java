package com.drunkenlion.alcoholfriday.domain.cart.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Schema(description = "장바구니에 추가할 상품 리스트 요청")
public class CartReqList {
    @Schema(description = "장바구니에 추가할 상품 리스트")
    private List<CartRequest> cartRequestList;
}
