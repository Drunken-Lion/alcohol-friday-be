package com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dto.response;

import com.drunkenlion.alcoholfriday.domain.restaurant.cart.entity.RestaurantOrderCartDetail;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "레스토랑 발주 장바구니 추가 반환 객체")
public class RestaurantOrderCartSaveResponse {
    @Schema(description = "제품 고유 식별 ID")
    private Long id;

    @Schema(description = "제품 이름")
    private String name;

    @Schema(description = "제품 제조사 이름")
    private String makerName;

    @Schema(description = "제품 객단가")
    private BigDecimal price;

    @Schema(description = "주문 가능 수량")
    private Long ableQuantity;

    @Schema(description = "주문 요청 수량")
    private Long quantity;

    @Schema(description = "제품 사진")
    private NcpFileResponse file;

    public static RestaurantOrderCartSaveResponse of(RestaurantOrderCartDetail restaurantOrderCartDetail) {
        return RestaurantOrderCartSaveResponse.builder()
                .id(restaurantOrderCartDetail.getProduct().getId())
                .name(restaurantOrderCartDetail.getProduct().getName())
                .makerName(restaurantOrderCartDetail.getProduct().getMaker().getName())
                .price(restaurantOrderCartDetail.getProduct().getDistributionPrice())
                .ableQuantity(restaurantOrderCartDetail.getProduct().getQuantity())
                .quantity(restaurantOrderCartDetail.getQuantity())
                .build();
    }

    public static RestaurantOrderCartSaveResponse of(RestaurantOrderCartDetail restaurantOrderCartDetail, NcpFileResponse file) {
        return RestaurantOrderCartSaveResponse.builder()
                .id(restaurantOrderCartDetail.getProduct().getId())
                .name(restaurantOrderCartDetail.getProduct().getName())
                .makerName(restaurantOrderCartDetail.getProduct().getMaker().getName())
                .price(restaurantOrderCartDetail.getProduct().getDistributionPrice())
                .ableQuantity(restaurantOrderCartDetail.getProduct().getQuantity())
                .quantity(restaurantOrderCartDetail.getQuantity())
                .file(file)
                .build();
    }
}
