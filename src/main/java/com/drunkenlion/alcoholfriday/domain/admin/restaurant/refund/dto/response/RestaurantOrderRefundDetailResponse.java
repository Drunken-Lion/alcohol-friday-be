package com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dto.response;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.entity.RestaurantOrderRefundDetail;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "매장 재고 환불 제품 항목")
public class RestaurantOrderRefundDetailResponse {
    @Schema(description = "제품 이름")
    private String productName;

    @Schema(description = "주문 객단가")
    private BigDecimal price;

    @Schema(description = "환불 수량")
    private Long quantity;

    @Schema(description = "제품 이미지")
    private NcpFileResponse file;

    public static RestaurantOrderRefundDetailResponse of(RestaurantOrderRefundDetail refundDetail, NcpFileResponse file) {
        return RestaurantOrderRefundDetailResponse.builder()
                .productName(refundDetail.getProduct().getName())
                .price(refundDetail.getPrice())
                .quantity(refundDetail.getQuantity())
                .file(file)
                .build();
    }
}
