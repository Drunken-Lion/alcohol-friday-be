package com.drunkenlion.alcoholfriday.domain.admin.restaurant.dto;

import com.drunkenlion.alcoholfriday.domain.restaurant.entity.RestaurantStock;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "매장 재고 상품 간략 정보")
public class RestaurantStockItemResponse {
    @Schema(description = "매장 재고 상품 고유 아이디")
    private Long stockItemId;

    @Schema(description = "매장 재고 상품 이름")
    private String stockItemName;

    @Schema(description = "매장 재고량")
    private Long stockQuantity;

    @Schema(description = "매장 재고 상품 이미지")
    private NcpFileResponse stockItemFile;

    public static RestaurantStockItemResponse of(RestaurantStock restaurantStock, NcpFileResponse file) {
        return RestaurantStockItemResponse.builder()
                .stockItemId(restaurantStock.getItem().getId())
                .stockItemName(restaurantStock.getItem().getName())
                .stockQuantity(restaurantStock.getQuantity())
                .stockItemFile(file)
                .build();
    }
}
