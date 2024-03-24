package com.drunkenlion.alcoholfriday.domain.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Schema(description = "주문에 추가할 배송 정보 (결제 페이지)")
public class OrderAddressRequest {
    @Schema(description = "주문 고유번호", example = "240314-221628-987501-1")
    private String orderNo;

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
    private String postcode;
}
