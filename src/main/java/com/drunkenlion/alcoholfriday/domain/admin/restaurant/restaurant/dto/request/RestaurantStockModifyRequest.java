package com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "매장 재고 수정 요청 항목")
public class RestaurantStockModifyRequest {
    @Schema(description = "매장 재고 고유아이디")
    private Long id;

    @Schema(description = "수정할 제품 판매 단가")
    private BigDecimal price;

    @Schema(description = "수정할 재고 수량")
    private Long quantity;
}
