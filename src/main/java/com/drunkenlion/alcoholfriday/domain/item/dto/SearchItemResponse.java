package com.drunkenlion.alcoholfriday.domain.item.dto;

import com.drunkenlion.alcoholfriday.domain.category.dto.FindCategoryResponse;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import lombok.*;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

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
    private List<NcpFileResponse> files;

    public static Page<SearchItemResponse> of(Page<Item> items, List<NcpFileResponse> files) {
        return items.map(item -> SearchItemResponse.of(item, files));
    }

    public static Page<SearchItemResponse> of(Page<Item> items) {
        return items.map((SearchItemResponse::of));
    }

    public static SearchItemResponse of(Item item, List<NcpFileResponse> files) {
        return SearchItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .info(item.getInfo())
                .category(FindCategoryResponse.of(item.getCategory()))
                .files(files)
                .build();
    }

    public static SearchItemResponse of(Item item) {
        return SearchItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .info(item.getInfo())
                .category(FindCategoryResponse.of(item.getCategory()))
                .build();
    }
}
