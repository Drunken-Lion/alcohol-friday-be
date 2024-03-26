package com.drunkenlion.alcoholfriday.domain.admin.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "제품 재고 입고 항목")
public class ProductQuantityRequest {
    @Schema(description = "재고 입고 수량")
    private Long quantity;
}
