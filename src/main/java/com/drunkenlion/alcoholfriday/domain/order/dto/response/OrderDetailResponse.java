package com.drunkenlion.alcoholfriday.domain.order.dto.response;

import com.drunkenlion.alcoholfriday.domain.item.dto.FindItemResponse;
import com.drunkenlion.alcoholfriday.domain.order.entity.OrderDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Schema(description = "사용자의 주문 상품 세부사항 항목")
public class OrderDetailResponse {
    @Schema(description = "상품 정보")
    private FindItemResponse item;
    @Schema(description = "주문 상품 수량")
    private Long quantity;
    @Schema(description = "총 상품 금액")
    private BigDecimal totalPrice;

    public static OrderDetailResponse of(OrderDetail orderDetail) {
        return OrderDetailResponse.builder()
                .item(FindItemResponse.of(orderDetail.getItem()))
                .quantity(orderDetail.getQuantity())
                .totalPrice(orderDetail.getTotalPrice())
                .build();
    }
}
