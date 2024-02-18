package com.drunkenlion.alcoholfriday.domain.cart.dto;

import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class CartReqList {
    private List<CartRequest> cartRequestList;
}
