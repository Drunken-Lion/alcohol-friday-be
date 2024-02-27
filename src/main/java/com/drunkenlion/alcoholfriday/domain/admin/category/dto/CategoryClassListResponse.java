package com.drunkenlion.alcoholfriday.domain.admin.category.dto;

import com.drunkenlion.alcoholfriday.domain.category.entity.CategoryClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "전체 카테고리 대분류 조회 항목")
public class CategoryClassListResponse {
    @Schema(description = "고유 아이디")
    private Long id;

    @Schema(description = "카테고리 대분류 이름")
    private String categoryFirstName;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "삭제여부")
    private boolean deleted;

    public static CategoryClassListResponse of(CategoryClass categoryClass) {
        return CategoryClassListResponse.builder()
                .id(categoryClass.getId())
                .categoryFirstName(categoryClass.getFirstName())
                .createdAt(categoryClass.getCreatedAt())
                .deleted(categoryClass.getDeletedAt() != null)
                .build();
    }
}
