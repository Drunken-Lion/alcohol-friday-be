package com.drunkenlion.alcoholfriday.domain.admin.product.dto;

import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.maker.entity.Maker;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "제품 수정 요청 항목")
public class ProductModifyRequest {
    @Schema(description = "카테고리 소분류 고유 아이디")
    private Long categoryLastId;

    @Schema(description = "제품명")
    private String name;

    @Schema(description = "제조사 고유 아이디")
    private Long makerId;

    @Schema(description = "제품 원가")
    private BigDecimal price;

    @Schema(description = "유통 가격")
    private BigDecimal distributionPrice;

    @Schema(description = "술 도수")
    private Double alcohol;

    @Schema(description = "제품 재료")
    private String ingredient;

    @Schema(description = "술 단맛")
    private Long sweet;

    @Schema(description = "술 신맛")
    private Long sour;

    @Schema(description = "술 청량감")
    private Long cool;

    @Schema(description = "술 바디감")
    private Long body;

    @Schema(description = "술 밸런스")
    private Long balance;

    @Schema(description = "술 향기")
    private Long incense;

    @Schema(description = "술 목넘김")
    private Long throat;

    @Schema(description = "이미지 삭제 목록")
    private List<Integer> remove;

    public static Product toEntity(ProductModifyRequest request, Category category, Maker maker) {
        return Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .distributionPrice(request.getDistributionPrice())
                .alcohol(request.getAlcohol())
                .ingredient(request.getIngredient())
                .sweet(request.getSweet())
                .sour(request.getSour())
                .cool(request.getCool())
                .body(request.getBody())
                .balance(request.getBalance())
                .incense(request.getIncense())
                .throat(request.getThroat())
                .category(category)
                .maker(maker)
                .build();
    }
}
