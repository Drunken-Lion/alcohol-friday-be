package com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "레스토랑 발주 장바구니에 제품 추가 요청")
public class RestaurantOrderCartSaveRequest {
    @Schema(description = "매장 고유 ID")
    private Long restaurantId;

    @Schema(description = "제품 고유 ID")
    private Long productId;

    @Schema(description = "주문 요청 수량")
    private Long quantity;
}
