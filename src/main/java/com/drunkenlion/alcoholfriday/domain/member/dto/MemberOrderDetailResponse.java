package com.drunkenlion.alcoholfriday.domain.member.dto;

import com.drunkenlion.alcoholfriday.domain.order.entity.OrderDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "하나의 주문한 상품 정보")
public class MemberOrderDetailResponse {
    @Schema(description = "주문한 상품 정보 고유 아이디")
    private Long id;

    @Schema(description = "상품 단가")
    private BigDecimal itemPrice;

    @Schema(description = "주문 수량")
    private Long quantity;

    @Schema(description = "총 상품 금액")
    private BigDecimal totalPrice;

    public static MemberOrderDetailResponse of(OrderDetail orderDetail) {
        return MemberOrderDetailResponse.builder()
                .id(orderDetail.getId())
                .itemPrice(orderDetail.getItemPrice())
                .quantity(orderDetail.getQuantity())
                .totalPrice(orderDetail.getTotalPrice())
                .build();
    }
}
