package com.drunkenlion.alcoholfriday.domain.admin.order.dto;

import com.drunkenlion.alcoholfriday.domain.order.entity.OrderDetail;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "주문 상품 항목")
public class OrderItemResponse {
    @Schema(description = "상품명")
    private String name;

    @Schema(description = "수량")
    private Long quantity;

    @Schema(description = "상품 객단가")
    private BigDecimal price;

    @Schema(description = "상품 총 금액")
    private BigDecimal totalPrice;

    @Schema(description = "상품 이미지")
    private NcpFileResponse itemFile;

    public static OrderItemResponse of(OrderDetail orderDetail, NcpFileResponse file) {
        return OrderItemResponse.builder()
                .name(orderDetail.getItem().getName())
                .quantity(orderDetail.getQuantity())
                .price(orderDetail.getItemPrice())
                .totalPrice(orderDetail.getTotalPrice())
                .itemFile(file)
                .build();
    }
}
