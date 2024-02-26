package com.drunkenlion.alcoholfriday.domain.member.dto;

import com.drunkenlion.alcoholfriday.domain.order.entity.OrderDetail;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberOrderDetailResponse {
    private Long id;
    private BigDecimal itemPrice;
    private Long quantity;
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
