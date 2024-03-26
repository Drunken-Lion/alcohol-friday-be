package com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dto.response;

import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Builder
@ToString
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "레스토랑의 제공하는 제품에 대한 응답")
public class ProductResponse {

    @Schema(description = "제품에 대한 고유 아이디")
    private Long id;

    @Schema(description = "제품의 이름")
    private String name;

    @Schema(description = "제품의 가격")
    private BigDecimal price;

    @Schema(description = "제품의 도수")
    private Double alcohol;

    @Schema(description = "레스토랑 재고 수량")
    private Long quantity;

    public static ProductResponse of(Product product, Long quantity) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .alcohol(product.getAlcohol())
                .quantity(quantity)
                .build();
    }

}
