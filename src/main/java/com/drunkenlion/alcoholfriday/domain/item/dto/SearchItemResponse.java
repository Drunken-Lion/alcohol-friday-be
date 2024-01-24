package com.drunkenlion.alcoholfriday.domain.item.dto;

import com.drunkenlion.alcoholfriday.domain.category.dto.FindCategoryResponse;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import lombok.*;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class SearchItemResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private String info;
    private FindCategoryResponse category;

    public static Page<SearchItemResponse> of (Page<Item> items) {
        return items.map((SearchItemResponse::of));
    }

    public static SearchItemResponse of(Item item) {
        return SearchItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .info(item.getInfo())
                .category(FindCategoryResponse.of(item.getCategory()))
                .build();
    }
}
