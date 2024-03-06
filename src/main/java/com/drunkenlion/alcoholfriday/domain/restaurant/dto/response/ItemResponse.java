package com.drunkenlion.alcoholfriday.domain.restaurant.dto.response;

import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemResponse {

    @Schema(description = "상품에 대한 고유 아이디")
    private Long id;

    @Schema(description = "상품의 이름")
    private String name;

    @Schema(description = "상품의 가격")
    private BigDecimal price;

    @Schema(description = "상품의 설명")
    private String info;

    public static ItemResponse of(Item item) {
        return ItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .info(item.getInfo()).build();

    }

}
