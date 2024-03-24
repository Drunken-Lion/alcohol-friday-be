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
@Schema(description = "레스토랑 발주 상세 목록 반환 객체")
public class RestaurantOrderDetailResponse {
    @Schema(description = "제품 고유 식별 ID")
    private Long id;

    @Schema(description = "제품 이름")
    private String name;

    @Schema(description = "제품 제조사 이름")
    private String makerName;

    @Schema(description = "제품 객단가")
    private BigDecimal price;

    @Schema(description = "제품 발주 수량")
    private Long orderQuantity;

    @Schema(description = "총 금액")
    private BigDecimal totalPrice;

    @Schema(description = "제품 사진")
    private NcpFileResponse file;

    public static RestaurantOrderDetailResponse of(RestaurantOrderDetail rod) {
        return RestaurantOrderDetailResponse.builder()
                .id(rod.getId())
                .name(rod.getProduct().getName())
                .makerName(rod.getProduct().getMaker().getName())
                .price(rod.getPrice())
                .orderQuantity(rod.getQuantity())
                .totalPrice(rod.getTotalPrice())
                .build();
    }

    public static RestaurantOrderDetailResponse of(RestaurantOrderDetail rod, NcpFileResponse file) {
        return RestaurantOrderDetailResponse.builder()
                .id(rod.getId())
                .name(rod.getProduct().getName())
                .makerName(rod.getProduct().getMaker().getName())
                .price(rod.getPrice())
                .orderQuantity(rod.getQuantity())
                .totalPrice(rod.getTotalPrice())
                .file(file)
                .build();
    }
}
