package com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dto.request;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.entity.RestaurantOrderRefund;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.entity.RestaurantOrderRefundDetail;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "매장 재고 환불 제품 등록 요청 항목")
public class RestaurantOrderRefundDetailCreateRequest {
    @Schema(description = "제품 고유 아이디")
    private Long productId;

    @Schema(description = "주문 객단가")
    private BigDecimal price;

    @Schema(description = "환불 가능 수량")
    private Long possibleQuantity;

    @Schema(description = "환불 수량")
    private Long quantity;

    public static RestaurantOrderRefundDetail toEntity(
            RestaurantOrderRefundDetailCreateRequest request,
            RestaurantOrderRefund restaurantOrderRefund,
            Product product
    ) {
        return RestaurantOrderRefundDetail.builder()
                .restaurantOrderRefund(restaurantOrderRefund)
                .product(product)
                .quantity(request.getQuantity())
                .price(request.getPrice())
                .totalPrice(request.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())))
                .build();
    }
}
