package com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dto.response;

import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "레스토랑 발주 제품 목록 반환 객체")
public class RestaurantOrderProductListResponse {
    @Schema(description = "제품 고유 식별 ID")
    private Long id;

    @Schema(description = "제품 이름")
    private String name;

    @Schema(description = "제품 제조사 이름")
    private String makerName;

    @Schema(description = "제품 객단가")
    private BigDecimal price;

    @Schema(description = "주문 가능 수량")
    private Long quantity;

    @Schema(description = "제품 사진")
    private NcpFileResponse file;

    public static RestaurantOrderProductListResponse of(Product product) {
        return RestaurantOrderProductListResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .makerName(product.getMaker().getName())
                .price(product.getDistributionPrice())
                .quantity(product.getQuantity())
                .build();
    }

    public static RestaurantOrderProductListResponse of(Product product, NcpFileResponse file) {
        return RestaurantOrderProductListResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .makerName(product.getMaker().getName())
                .price(product.getDistributionPrice())
                .quantity(product.getQuantity())
                .file(file)
                .build();
    }
}
