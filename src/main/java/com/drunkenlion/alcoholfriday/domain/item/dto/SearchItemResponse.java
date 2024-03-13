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
    private List<NcpFileResponse> files;

    public static Page<SearchItemResponse> of(Page<Item> items, List<NcpFileResponse> files) {
        return items.map(item -> SearchItemResponse.of(item, files));
    }

    public static Page<SearchItemResponse> of(Page<Item> items) {
        return items.map((SearchItemResponse::of));
    }

    public static SearchItemResponse of(Item item, List<NcpFileResponse> files) {
        List<NcpFileResponse> itemFile = files.stream()
                .map(file -> {
                    if (file != null && Objects.equals(item.getId(), file.getEntityId())) {
                        return file;
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();

        // 필터에서 null을 거르면 비어있는 files가 나와서 비어있는 files에 임의로 null 입력
        if (itemFile.isEmpty()) {
            itemFile = Collections.singletonList(null);
        }

        return SearchItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .category(FindCategoryResponse.of(item.getCategory()))
                .files(itemFile)
                .build();
    }

    public static SearchItemResponse of(Item item) {
        return SearchItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .category(FindCategoryResponse.of(item.getCategory()))
                .build();
    }
}
