package com.drunkenlion.alcoholfriday.domain.admin.category.dto;

import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "전체 카테고리 소분류 조회 항목")
public class CategoryListResponse {
    @Schema(description = "고유 아이디")
    private Long id;

    @Schema(description = "카테고리 대분류 이름")
    private String categoryFirstName;

    @Schema(description = "카테고리 소분류 이름")
    private String categoryLastName;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "삭제여부")
    private boolean deleted;

    public static CategoryListResponse of(Category category) {
        return CategoryListResponse.builder()
                .id(category.getId())
                .categoryFirstName(category.getCategoryClass().getFirstName())
                .categoryLastName(category.getLastName())
                .createdAt(category.getCreatedAt())
                .deleted(category.getDeletedAt() != null)
                .build();
    }
}
