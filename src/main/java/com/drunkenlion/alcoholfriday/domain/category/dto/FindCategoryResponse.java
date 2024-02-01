package com.drunkenlion.alcoholfriday.domain.category.dto;

import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import lombok.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class FindCategoryResponse {
    private String firstName;
    private String lastName;

    public static FindCategoryResponse of(Category category) {
        return FindCategoryResponse.builder()
                .firstName(category.getCategoryClass().getFirstName())
                .lastName(category.getLastName())
                .build();
    }
}
