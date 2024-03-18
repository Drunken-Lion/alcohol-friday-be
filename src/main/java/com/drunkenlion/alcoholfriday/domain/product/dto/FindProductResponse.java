package com.drunkenlion.alcoholfriday.domain.product.dto;

import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Schema(description = "제품 상세 요청하는 응답")
public class FindProductResponse {
    @Schema(description = "제품 이름")
    private String name;

    @Schema(description = "제품 수량")
    private Long quantity;

    @Schema(description = "알콜 도수")
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

    public static FindProductResponse of(Product product) {
        return FindProductResponse.builder()
                .name(product.getName())
                .quantity(product.getQuantity())
                .alcohol(product.getAlcohol())
                .ingredient(product.getIngredient())
                .sweet(product.getSweet())
                .sour(product.getSour())
                .cool(product.getCool())
                .body(product.getBody())
                .balance(product.getBalance())
                .incense(product.getIncense())
                .throat(product.getThroat())
                .build();
    }
}
