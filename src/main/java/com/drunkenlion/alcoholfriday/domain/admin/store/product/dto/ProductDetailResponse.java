package com.drunkenlion.alcoholfriday.domain.admin.store.product.dto;

import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "제품 상세 조회 항목")
public class ProductDetailResponse {
    @Schema(description = "고유 아이디")
    private Long id;

    @Schema(description = "카테고리 소분류 고유 아이디")
    private Long categoryLastId;

    @Schema(description = "카테고리 대분류 이름")
    private String categoryFirstName;

    @Schema(description = "카테고리 소분류 이름")
    private String categoryLastName;

    @Schema(description = "제품명")
    private String name;

    @Schema(description = "제조사 고유 아이디")
    private Long makerId;

    @Schema(description = "제조사 이름")
    private String makerName;

    @Schema(description = "제품 원가")
    private BigDecimal price;

    @Schema(description = "재고 수량")
    private Long quantity;

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
    private Long balence;

    @Schema(description = "술 향기")
    private Long insense;

    @Schema(description = "술 목넘김")
    private Long throat;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "마지막 수정일시")
    private LocalDateTime updatedAt;

    @Schema(description = "삭제일시")
    private LocalDateTime deletedAt;

    public static ProductDetailResponse of(Product product) {
        return ProductDetailResponse.builder()
                .id(product.getId())
                .categoryLastId(product.getCategory().getId())
                .categoryFirstName(product.getCategory().getCategoryClass().getFirstName())
                .categoryLastName(product.getCategory().getLastName())
                .name(product.getName())
                .makerId(product.getMaker().getId())
                .makerName(product.getMaker().getName())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .ingredient(product.getIngredient())
                .sweet(product.getSweet())
                .sour(product.getSour())
                .cool(product.getCool())
                .body(product.getBody())
                .balence(product.getBalence())
                .insense(product.getInsense())
                .throat(product.getThroat())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .deletedAt(product.getDeletedAt())
                .build();
    }
}
