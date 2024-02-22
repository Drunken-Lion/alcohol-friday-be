package com.drunkenlion.alcoholfriday.domain.cart.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Schema(description = "장바구니에 삭제할 상품 리스트 요청")
public class CartDeleteReqList {
    @Schema(description = "장바구니에 삭제할 상품 리스트")
    private List<DeleteCartRequest> cartDeleteReqList;
}
