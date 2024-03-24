package com.drunkenlion.alcoholfriday.domain.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Schema(description = "주문에 추가할 상품 리스트 요청")
public class OrderRequestList {
    @Schema(description = "주문에 추가할 상품 리스트")
    private List<OrderItemRequest> orderItemList;
}
