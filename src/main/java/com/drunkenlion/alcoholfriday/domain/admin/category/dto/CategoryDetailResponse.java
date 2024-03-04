package com.drunkenlion.alcoholfriday.domain.admin.category.dto;

import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "카테고리 소분류 상세 조회 항목")
public class CategoryDetailResponse {
    @Schema(description = "고유 아이디")
    private Long id;

    @Schema(description = "카테고리 대분류 고유 아이디")
    private Long categoryFirstId;

    @Schema(description = "카테고리 대분류 이름")
    private String categoryFirstName;

    @Schema(description = "카테고리 소분류 이름")
    private String categoryLastName;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "마지막 수정일시")
    private LocalDateTime updatedAt;

    @Schema(description = "삭제일시")
    private LocalDateTime deletedAt;

    public static CategoryDetailResponse of(Category category) {
        return CategoryDetailResponse.builder()
                .id(category.getId())
                .categoryFirstId(category.getCategoryClass().getId())
                .categoryFirstName(category.getCategoryClass().getFirstName())
                .categoryLastName(category.getLastName())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .deletedAt(category.getDeletedAt())
                .build();
    }
}
