package com.drunkenlion.alcoholfriday.domain.category.dto;

import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Schema(description = "상품이나 제품의 카테고리")
public class FindCategoryResponse {
    @Schema(description = "대분류")
    private String firstName;

    @Schema(description = "소분류")
    private String lastName;

    public static FindCategoryResponse of(Category category) {
        return FindCategoryResponse.builder()
                .firstName(category.getCategoryClass().getFirstName())
                .lastName(category.getLastName())
                .build();
    }
}
