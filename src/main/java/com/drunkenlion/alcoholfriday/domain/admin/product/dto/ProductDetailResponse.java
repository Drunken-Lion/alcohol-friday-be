package com.drunkenlion.alcoholfriday.domain.admin.product.dto;

import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
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

    @Schema(description = "유통 가격")
    private BigDecimal distributionPrice;

    @Schema(description = "재고 수량")
    private Long quantity;

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

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "마지막 수정일시")
    private LocalDateTime updatedAt;

    @Schema(description = "삭제일시")
    private LocalDateTime deletedAt;

    @Schema(description = "제품 이미지")
    private NcpFileResponse productFiles;

    public static ProductDetailResponse of(Product product, NcpFileResponse file) {
        return ProductDetailResponse.builder()
                .id(product.getId())
                .categoryLastId(product.getCategory().getId())
                .categoryFirstName(product.getCategory().getCategoryClass().getFirstName())
                .categoryLastName(product.getCategory().getLastName())
                .name(product.getName())
                .makerId(product.getMaker().getId())
                .makerName(product.getMaker().getName())
                .price(product.getPrice())
                .distributionPrice(product.getDistributionPrice())
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
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .deletedAt(product.getDeletedAt())
                .productFiles(file)
                .build();
    }
}
