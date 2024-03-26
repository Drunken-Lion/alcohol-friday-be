package com.drunkenlion.alcoholfriday.domain.admin.product.dto;

import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "제품 재고 조회 항목")
public class ProductQuantityResponse {
    @Schema(description = "제품명")
    private String name;

    @Schema(description = "재고 수량")
    private Long quantity;

    public static ProductQuantityResponse of(Product product) {
        return ProductQuantityResponse.builder()
                .name(product.getName())
                .quantity(product.getQuantity())
                .build();
    }
}
