package com.drunkenlion.alcoholfriday.domain.admin.item.dto;

import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "전체 상품 조회 항목")
public class ItemListResponse {
    @Schema(description = "고유 아이디")
    private Long id;

    @Schema(description = "카테고리 소분류 이름")
    private String categoryLastName;

    @Schema(description = "상품명")
    private String name;

    @Schema(description = "가격")
    private BigDecimal price;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "삭제여부")
    private boolean deleted;

    public static ItemListResponse of(Item item) {
        return ItemListResponse.builder()
                .id(item.getId())
                .categoryLastName(item.getCategory().getLastName())
                .name(item.getName())
                .price(item.getPrice())
                .createdAt(item.getCreatedAt())
                .deleted(item.getDeletedAt() != null)
                .build();
    }
}
