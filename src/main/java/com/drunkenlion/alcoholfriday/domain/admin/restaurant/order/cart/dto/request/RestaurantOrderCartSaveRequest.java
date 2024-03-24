package com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.cart.dto.request;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dto.request.RestaurantOrderRefundDetailCreateRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "레스토랑 발주 장바구니에 제품 추가 요청")
public class RestaurantOrderCartSaveRequest {
    @Schema(description = "주문 요청 수량")
    private Long quantity;

    @Schema(description = "제품 고유 ID")
    private Long productId;

    @Schema(description = "매장 고유 ID")
    private Long restaurantId;
}
