package com.drunkenlion.alcoholfriday.domain.order.dto;

import com.drunkenlion.alcoholfriday.domain.order.entity.OrderDetail;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderDetailResponse {
    @Schema(description = "주문한 상품 정보 고유 아이디")
    private Long id;

    @Schema(description = "상품 이름")
    private String name;

    @Schema(description = "주문 수량")
    private Long quantity;

    @Schema(description = "총 상품 금액")
    private BigDecimal totalPrice;

    @Schema(description = "썸네일 이미지 파일 정보")
    private NcpFileResponse file;

    public static OrderDetailResponse of(OrderDetail orderDetail) {
        return OrderDetailResponse.builder()
                .id(orderDetail.getId())
                .name(orderDetail.getItem().getName())
                .quantity(orderDetail.getQuantity())
                .totalPrice(orderDetail.getTotalPrice())
                .build();
    }

    public static OrderDetailResponse of(OrderDetail orderDetail, NcpFileResponse fileResponse) {
        return OrderDetailResponse.builder()
                .id(orderDetail.getId())
                .name(orderDetail.getItem().getName())
                .quantity(orderDetail.getQuantity())
                .totalPrice(orderDetail.getTotalPrice())
                .file(fileResponse)
                .build();
    }
}
