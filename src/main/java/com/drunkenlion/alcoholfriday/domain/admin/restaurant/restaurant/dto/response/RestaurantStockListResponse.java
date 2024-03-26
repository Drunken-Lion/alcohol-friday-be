package com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.dto.response;

import com.drunkenlion.alcoholfriday.domain.restaurant.entity.RestaurantStock;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "매장의 재고 조회 항목")
public class RestaurantStockListResponse {
    @Schema(description = "재고 고유 아이디")
    private Long id;

    @Schema(description = "제품명")
    private String name;

    @Schema(description = "제품 판매 단가")
    private BigDecimal price;

    @Schema(description = "재고")
    private Long quantity;

    @Schema(description = "이미지 파일 정보")
    private NcpFileResponse file;

    public static RestaurantStockListResponse of(RestaurantStock stock, NcpFileResponse file) {
        return RestaurantStockListResponse.builder()
                .id(stock.getId())
                .name(stock.getProduct().getName())
                .price(stock.getPrice())
                .quantity(stock.getQuantity())
                .file(file)
                .build();
    }
}
