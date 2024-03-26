package com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dto.response;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.entity.RestaurantOrderCartDetail;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "매장 장바구니 목록 반환 객체")
public class RestaurantOrderCartListResponse {
    @Schema(description = "장바구니 목록 고유 식별 ID")
    private Long id;

    @Schema(description = "제품 고유 식별 ID")
    private Long productId;

    @Schema(description = "제품명")
    private String productName;

    @Schema(description = "제조사명")
    private String makerName;

    @Schema(description = "판매 단가")
    private BigDecimal price;

    @Schema(description = "담은 수량")
    private Long quantity;

    @Schema(description = "총 금액")
    private BigDecimal totalPrice;

    @Schema(description = "주문 가능 수량")
    private Long ableQuantity;

    @Schema(description = "제품 이미지")
    private NcpFileResponse files;

    public static RestaurantOrderCartListResponse of(RestaurantOrderCartDetail detail, NcpFileResponse files) {
        return RestaurantOrderCartListResponse.builder()
                .id(detail.getId())
                .productId(detail.getProduct().getId())
                .productName(detail.getProduct().getName())
                .makerName(detail.getProduct().getMaker().getName())
                .price(detail.getProduct().getDistributionPrice())
                .ableQuantity(detail.getProduct().getQuantity())
                .quantity(detail.getQuantity())
                .totalPrice(detail.getProduct().getDistributionPrice().multiply(BigDecimal.valueOf(detail.getQuantity())))
                .files(files)
                .build();
    }
}
