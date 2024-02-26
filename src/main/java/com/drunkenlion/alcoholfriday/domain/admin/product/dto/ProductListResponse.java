package com.drunkenlion.alcoholfriday.domain.admin.product.dto;

import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "전체 제품 조회 항목")
public class ProductListResponse {
    @Schema(description = "고유 아이디")
    private Long id;

    @Schema(description = "카테고리 소분류 이름")
    private String categoryLastName;

    @Schema(description = "제품명")
    private String name;

    @Schema(description = "제조사 이름")
    private String makerName;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "삭제여부")
    private boolean deleted;

    public static ProductListResponse of(Product product) {
        return ProductListResponse.builder()
                .id(product.getId())
                .categoryLastName(product.getCategory().getLastName())
                .name(product.getName())
                .makerName(product.getMaker().getName())
                .createdAt(product.getCreatedAt())
                .deleted(product.getDeletedAt() != null)
                .build();
    }
}
