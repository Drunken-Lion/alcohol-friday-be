package com.drunkenlion.alcoholfriday.domain.admin.order.dto;

import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "주문 수정 요청 항목")
public class OrderModifyRequest {
    @Schema(description = "배송시 받는 사람")
    private String recipient;

    @Schema(description = "연락처")
    private Long phone;

    @Schema(description = "배송지 주소")
    private String address;

    @Schema(description = "배송지 상세 주소")
    private String addressDetail;

    @Schema(description = "배송지 우편번호")
    private String postcode;

    @Schema(description = "배송지 메모")
    private String description;

    public static Order toEntity(OrderModifyRequest request) {
        return Order.builder()
                .recipient(request.getRecipient())
                .phone(request.getPhone())
                .address(request.getAddress())
                .addressDetail(request.getAddressDetail())
                .postcode(request.getPostcode())
                .description(request.getDescription())
                .build();
    }
}
