package com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.response;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity.RestaurantOrderDetail;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "레스토랑 발주 제품 정보 (Owner)")
public class OwnerRestaurantOrderDetailResponse {
    @Schema(description = "발주 제품 고유아이디")
    private Long id;

    @Schema(description = "발주한 제품 이름")
    private String productName;

    @Schema(description = "발주한 제품 단가")
    private BigDecimal price;

    @Schema(description = "발주한 제품 총 금액")
    private BigDecimal totalPrice;

    @Schema(description = "발주한 제품 수량")
    private Long quantity;

    @Schema(description = "환불 가능한 수량")
    private Long refundQuantity;

    @Schema(description = "이미지 파일 정보")
    private NcpFileResponse file;

    public static OwnerRestaurantOrderDetailResponse of(RestaurantOrderDetail restaurantOrderDetail,
                                                        Long refundQuantity,
                                                        NcpFileResponse ncpFileResponse) {

        return OwnerRestaurantOrderDetailResponse.builder()
                .id(restaurantOrderDetail.getId())
                .productName(restaurantOrderDetail.getProduct().getName())
                .price(restaurantOrderDetail.getPrice())
                .totalPrice(restaurantOrderDetail.getTotalPrice())
                .quantity(restaurantOrderDetail.getQuantity())
                .refundQuantity(refundQuantity)
                .file(ncpFileResponse)
                .build();
    }
}
