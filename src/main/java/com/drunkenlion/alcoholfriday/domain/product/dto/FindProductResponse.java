package com.drunkenlion.alcoholfriday.domain.product.dto;

import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import lombok.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class FindProductResponse {
    private String name;
    private Long quantity;
    private Long alcohol;
    private String ingredient;
    private Long sweet;
    private Long sour;
    private Long cool;
    private Long body;
    private Long balence;
    private Long insense;
    private Long throat;

    public static FindProductResponse of(Product product) {
        return FindProductResponse.builder()
                .name(product.getName())
                .quantity(product.getQuantity())
                .alcohol(product.getAlcohol())
                .ingredient(product.getIngredient())
                .sweet(product.getSweet())
                .sour(product.getSour())
                .cool(product.getCool())
                .body(product.getBody())
                .balence(product.getBalence())
                .insense(product.getInsense())
                .throat(product.getThroat())
                .build();
    }
}
