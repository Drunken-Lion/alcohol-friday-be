package com.drunkenlion.alcoholfriday.domain.order.dto;

import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import com.drunkenlion.alcoholfriday.global.common.enumerated.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderResponse {
    @Schema(description = "주문 내역 고유 아이디")
    private Long id;

    @Schema(description = "주문 번호")
    private String orderNo;

    @Schema(description = "주문 상태")
    private OrderStatus orderStatus;

    @Schema(description = "주문 상품 총 금액")
    private BigDecimal price;

    @Schema(description = "배송 금액")
    private BigDecimal deliveryPrice;

    @Schema(description = "배송비 포함 주문 총 금액")
    private BigDecimal totalPrice;

    @Schema(description = "배송받는 사람")
    private String recipient;

    @Schema(description = "배송받는 사람 연락처")
    private Long phone;

    @Schema(description = "배송지 우편번호")
    private String postcode;

    @Schema(description = "배송지 주소")
    private String address;

    @Schema(description = "배송지 상세 주소")
    private String addressDetail;

    @Schema(description = "배송 시 주의사항")
    private String description;

    @Schema(description = "주문 취소 사유")
    private String cancelReason;

    @Schema(description = "주문 일자")
    private LocalDateTime createdAt;

    @Schema(description = "주문한 상품 정보 목록")
    private List<OrderDetailResponse> orderDetails;

    public static OrderResponse of(Order order, List<OrderDetailResponse> orderDetailResponses) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .orderStatus(order.getOrderStatus())
                .price(order.getPrice())
                .deliveryPrice(order.getDeliveryPrice())
                .totalPrice(order.getTotalPrice())
                .recipient(order.getRecipient())
                .phone(order.getPhone())
                .postcode(order.getPostcode())
                .address(order.getAddress())
                .addressDetail(order.getAddressDetail())
                .description(order.getDescription())
                .cancelReason(order.getCancelReason())
                .createdAt(order.getCreatedAt())
                .orderDetails(orderDetailResponses)
                .build();
    }

    public static OrderResponse of(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .orderStatus(order.getOrderStatus())
                .price(order.getPrice())
                .deliveryPrice(order.getDeliveryPrice())
                .totalPrice(order.getTotalPrice())
                .recipient(order.getRecipient())
                .phone(order.getPhone())
                .postcode(order.getPostcode())
                .address(order.getAddress())
                .addressDetail(order.getAddressDetail())
                .description(order.getDescription())
                .cancelReason(order.getCancelReason())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
