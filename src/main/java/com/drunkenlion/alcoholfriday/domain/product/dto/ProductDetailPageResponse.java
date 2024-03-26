package com.drunkenlion.alcoholfriday.domain.product.dto;

import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
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
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "제품 상세 페이지")
public class ProductDetailPageResponse {
    @Schema(description = "고유 식별 ID")
    private Long id;

    @Schema(description = "이름")
    private String name;

    @Schema(description = "유통가격")
    private BigDecimal price;

    @Schema(description = "카테고리 이름")
    private String categoryName;

    @Schema(description = "재료")
    private String ingredient;

    @Schema(description = "제조사 이름")
    private String makerName;

    @Schema(description = "도수")
    private Double alcohol;

    @Schema(description = "단맛")
    private Long sweet;

    @Schema(description = "신맛")
    private Long sour;

    @Schema(description = "청량감")
    private Long cool;

    @Schema(description = "바디감")
    private Long body;

    @Schema(description = "밸런스")
    private Long balance;

    @Schema(description = "향기")
    private Long incense;

    @Schema(description = "목넘김")
    private Long throat;

    @Schema(description = "제품 사진")
    public NcpFileResponse files;

    public static ProductDetailPageResponse of(Product product, NcpFileResponse files) {
        return ProductDetailPageResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getDistributionPrice())
                .categoryName(product.getCategory().getLastName())
                .ingredient(product.getIngredient())
                .makerName(product.getMaker().getName())
                .alcohol(product.getAlcohol())
                .sweet(product.getSweet())
                .sour(product.getSour())
                .cool(product.getCool())
                .body(product.getBody())
                .balance(product.getBalance())
                .incense(product.getIncense())
                .throat(product.getThroat())
                .files(files)
                .build();
    }
}
