package com.drunkenlion.alcoholfriday.domain.item.dto;

import com.drunkenlion.alcoholfriday.domain.category.dto.FindCategoryResponse;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Schema(description = "검색어로 전체 상품을 조회하는 응답")
public class SearchItemResponse {
    @Schema(description = "상품의 식별자")
    private Long id;

    @Schema(description = "상품의 이름")
    private String name;

    @Schema(description = "상품의 가격")
    private BigDecimal price;

    @Schema(description = "상품의 카테고리")
    private FindCategoryResponse category;

    @Schema(description = "상품에 포함된 이미지")
    private NcpFileResponse files;

    @Schema(description = "상품 총 평점 / 상품 리뷰 총 개수")
    private ItemRating itemRating;

    public static Page<SearchItemResponse> of(Page<Item> items, List<NcpFileResponse> files, List<ItemRating> itemRatingList) {
        return items.map(item -> SearchItemResponse.of(item, files, itemRatingList));
    }

    public static Page<SearchItemResponse> of(Page<Item> items) {
        return items.map((SearchItemResponse::of));
    }

    public static SearchItemResponse of(Item item) {
        return SearchItemResponse.of(item, Collections.singletonList(null));
    }

    public static SearchItemResponse of(Item item, List<NcpFileResponse> files, List<ItemRating> itemRatingList) {
        ItemRating foundItemRating = getFoundItemRating(item, itemRatingList);

        return SearchItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .category(FindCategoryResponse.of(item.getCategory()))
                .files(files.get(0))
                .itemRating(foundItemRating)
                .build();
    }

    public static SearchItemResponse of(Item item, List<ItemRating> itemRatingList) {
        ItemRating foundItemRating = getFoundItemRating(item, itemRatingList);

        return SearchItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .category(FindCategoryResponse.of(item.getCategory()))
                .itemRating(foundItemRating)
                .build();
    }

    // 리뷰 평점 & 리뷰 개수
    private static ItemRating getFoundItemRating(Item item, List<ItemRating> itemRatingList) {
        return itemRatingList.stream()
                .map(itemRating -> {
                    if (itemRating != null && Objects.equals(item.getId(), itemRating.getItemId())) {
                        return itemRating;
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
}