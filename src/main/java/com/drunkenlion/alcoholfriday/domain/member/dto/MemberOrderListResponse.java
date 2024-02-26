package com.drunkenlion.alcoholfriday.domain.member.dto;

import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "한 건의 주문 내역")
public class MemberOrderListResponse {
    @Schema(description = "주문 내역 고유 아이디")
    private Long id;

    @Schema(description = "주문 번호")
    private String orderNo;

    @Schema(description = "주문 상태")
    private String orderStatus;

    @Schema(description = "총 주문 금액")
    private BigDecimal orderPrice;

    @Schema(description = "배송받는 사람")
    private String recipient;

    @Schema(description = "배송받는 사람 연락처")
    private Long phone;

    @Schema(description = "배송지 우편번호")
    private Long postcode;

    @Schema(description = "배송지 주소")
    private String address;

    @Schema(description = "배송지 상세 주소")
    private String detailAddress;

    @Schema(description = "배송 시 주의사항")
    private String description;

    @Schema(description = "주문한 상품 정보 목록")
    private List<MemberOrderDetailResponse> orderDetails;

    public static MemberOrderListResponse of(Order order) {
        List<MemberOrderDetailResponse> orderDetailResponses =
                order.getOrderDetails().stream().map(MemberOrderDetailResponse::of).toList();

        return MemberOrderListResponse.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .orderStatus(order.getOrderStatus().name())
                .orderPrice(order.getPrice())
                .recipient(order.getRecipient())
                .phone(order.getPhone())
                .postcode(order.getPostcode())
                .address(order.getAddress())
                .detailAddress(order.getDetail())
                .description(order.getDescription())
                .orderDetails(orderDetailResponses)
                .build();
    }
}
