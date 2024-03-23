package com.drunkenlion.alcoholfriday.domain.review.dto.response;

import com.drunkenlion.alcoholfriday.domain.order.dto.OrderDetailResponse;
import com.drunkenlion.alcoholfriday.domain.order.entity.OrderDetail;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewOrderDetailResponse {
    @Schema(description = "주문 상세 고유 식별 ID")
    private Long orderDetailId;

    @Schema(description = "상품 이름")
    private String itemName;

    @Schema(description = "상품 금액")
    private BigDecimal itemPrice;

    @Schema(description = "주문 수량")
    private Long quantity;

    @Schema(description = "상품 대표 이미지")
    private NcpFileResponse file;

    public static ReviewOrderDetailResponse of(OrderDetail orderDetail) {
        return ReviewOrderDetailResponse.builder()
                .orderDetailId(orderDetail.getId())
                .itemName(orderDetail.getItem().getName())
                .itemPrice(orderDetail.getItemPrice())
                .quantity(orderDetail.getQuantity())
                .build();
    }
    public static ReviewOrderDetailResponse of(OrderDetail orderDetail, NcpFileResponse file) {
        return ReviewOrderDetailResponse.builder()
                .orderDetailId(orderDetail.getId())
                .itemName(orderDetail.getItem().getName())
                .itemPrice(orderDetail.getItemPrice())
                .quantity(orderDetail.getQuantity())
                .file(file)
                .build();
    }
}
