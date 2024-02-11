package com.drunkenlion.alcoholfriday.domain.item.dto;

import com.drunkenlion.alcoholfriday.domain.category.dto.FindCategoryResponse;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.item.entity.ItemProduct;
import com.drunkenlion.alcoholfriday.domain.product.dto.FindProductResponse;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class FindItemResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private String info;
    private FindCategoryResponse category;
    private List<FindProductResponse> products;
    private NcpFileResponse file;

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
