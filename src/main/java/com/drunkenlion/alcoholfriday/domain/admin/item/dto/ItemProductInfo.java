package com.drunkenlion.alcoholfriday.domain.admin.item.dto;

import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.item.entity.ItemProduct;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "상품의 제품 항목")
public class ItemProductInfo {
    @Schema(description = "제품의 고유 아이디")
    private Long productId;

    @Schema(description = "수량")
    private Long quantity;

    public static ItemProductInfo of(ItemProduct itemProduct) {
        return ItemProductInfo.builder()
                .productId(itemProduct.getProduct().getId())
                .quantity(itemProduct.getQuantity())
                .build();
    }

    public static ItemProduct toEntity(ItemProductInfo request, Item item, Product product) {
        return ItemProduct.builder()
                .item(item)
                .product(product)
                .quantity(request.getQuantity())
                .build();
    }
}
