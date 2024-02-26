package com.drunkenlion.alcoholfriday.domain.admin.category.dto;

import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.category.entity.CategoryClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "카테고리 소분류 입력 요청 항목")
public class CategoryRequest {
    @Schema(description = "카테고리 대분류 고유 아이디")
    private Long categoryFirstId;

    @Schema(description = "카테고리 소분류 이름")
    private String categoryLastName;

    public static Category toEntity(CategoryRequest request, CategoryClass categoryClass) {
        return Category.builder()
                .categoryClass(categoryClass)
                .lastName(request.getCategoryLastName())
                .build();
    }
}
