package com.drunkenlion.alcoholfriday.domain.admin.category.dto;

import com.drunkenlion.alcoholfriday.domain.category.entity.CategoryClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "카테고리 대분류 입력 요청 항목")
public class CategoryClassRequest {
    @Schema(description = "카테고리 대분류 이름")
    private String categoryFirstName;

    public static CategoryClass toEntity(CategoryClassRequest request) {
        return CategoryClass.builder()
                .firstName(request.getCategoryFirstName())
                .build();
    }
}
