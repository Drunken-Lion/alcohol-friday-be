package com.drunkenlion.alcoholfriday.domain.cart.dto;

import lombok.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class AddCartRequest {
    private Long itemId;
    private Long quantity;
}
