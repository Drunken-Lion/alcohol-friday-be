package com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "레스토랑 발주 장바구니에 제품 개수 수정 요청")
public class RestaurantOrderCartUpdateRequest {
    @Schema(description = "수정 수량")
    private Long quantity;
}
