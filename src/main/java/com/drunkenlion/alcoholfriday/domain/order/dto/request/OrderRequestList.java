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

    @Schema(description = "배송받는 사람")
    private String recipient;

    @Schema(description = "배송받는 사람의 연락처")
    private Long phone;

    @Schema(description = "배송지 주소")
    private String address;

    @Schema(description = "배송지 상세 주소")
    private String addressDetail;

    @Schema(description = "배송시 주의사항")
    private String description;

    @Schema(description = "배송지 우편번호")
    private Long postcode;
}
