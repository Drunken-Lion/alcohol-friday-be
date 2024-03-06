package com.drunkenlion.alcoholfriday.domain.restaurant.dto.response;

import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import lombok.*;
import java.math.BigDecimal;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponse {

    private Long id;

    private String name;

    private BigDecimal price;

    private String info;

    public static ItemResponse of(Item item) {
        return ItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .info(item.getInfo()).build();

    }

}
