package com.drunkenlion.alcoholfriday.domain.item.dto;

import com.drunkenlion.alcoholfriday.domain.category.dto.FindCategoryResponse;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.item.entity.ItemProduct;
import com.drunkenlion.alcoholfriday.domain.product.dto.FindProductResponse;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Schema(description = "상품 상세 조회하는 요청")
public class FindItemResponse {
    @Schema(description = "상품의 식별자")
    private Long id;

    @Schema(description = "상품 이름")
    private String name;

    @Schema(description = "상품 가격")
    private BigDecimal price;

    @Schema(description = "상품 설명")
    private String info;

    @Schema(description = "상품의 카테고리")
    private FindCategoryResponse category;

    @Schema(description = "상품에 포함된 제품(WMS에서 관리하는 제품)")
    private List<FindProductResponse> products;

    @Schema(description = "상품에 포함된 파일")
    private NcpFileResponse file;

    @Schema(description = "상품 총 평점 / 상품 리뷰 총 개수")
    private ItemRating itemRating;

    public static FindItemResponse of(Item item, NcpFileResponse file, ItemRating itemRating) {
        List<FindProductResponse> list = item.getItemProducts().stream()
                .map(ItemProduct::getProduct)
                .map(FindProductResponse::of)
                .toList();

        return FindItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .info(item.getInfo())
                .category(FindCategoryResponse.of(item.getCategory()))
                .products(list)
                .file(file)
                .itemRating(itemRating)
                .build();
    }

    public static FindItemResponse of(Item item, NcpFileResponse file) {
        List<FindProductResponse> list = item.getItemProducts().stream()
                .map(ItemProduct::getProduct)
                .map(FindProductResponse::of)
                .toList();

        return FindItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .info(item.getInfo())
                .category(FindCategoryResponse.of(item.getCategory()))
                .products(list)
                .file(file)
                .build();
    }

    public static FindItemResponse of(Item item, ItemRating itemRating) {
        List<FindProductResponse> list = item.getItemProducts().stream()
                .map(ItemProduct::getProduct)
                .map(FindProductResponse::of)
                .toList();

        return FindItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .info(item.getInfo())
                .category(FindCategoryResponse.of(item.getCategory()))
                .products(list)
                .itemRating(itemRating)
                .build();
    }

    public static FindItemResponse of(Item item) {
        List<FindProductResponse> list = item.getItemProducts().stream()
                .map(ItemProduct::getProduct)
                .map(FindProductResponse::of)
                .toList();

        return FindItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .info(item.getInfo())
                .category(FindCategoryResponse.of(item.getCategory()))
                .products(list)
                .build();
    }
}
