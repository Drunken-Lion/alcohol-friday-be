package com.drunkenlion.alcoholfriday.domain.member.dto;

import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberOrderListResponse {
    private Long id;
    private String orderNo;
    private String orderStatus;
    private BigDecimal orderPrice;
    private String recipient;
    private Long phone;
    private Long postcode;
    private String address;
    private String detailAddress;
    private String description;
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
