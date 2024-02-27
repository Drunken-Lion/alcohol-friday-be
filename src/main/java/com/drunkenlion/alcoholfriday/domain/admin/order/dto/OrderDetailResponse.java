package com.drunkenlion.alcoholfriday.domain.admin.order.dto;

import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import com.drunkenlion.alcoholfriday.global.common.enumerated.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "주문 상세 조회 항목")
public class OrderDetailResponse {
    @Schema(description = "고유 아이디")
    private Long id;

    @Schema(description = "주문번호")
    private String orderNo;

    @Schema(description = "주문자")
    private String customerName;

    @Schema(description = "주문상태")
    private OrderStatus orderStatus;

    @Schema(description = "상품 상세")
    private List<OrderItemResponse> orderItems;

    // TODO: 배송비 추가 필요

    @Schema(description = "배송시 받는 사람")
    private String recipient;

    @Schema(description = "연락처")
    private Long phone;

    @Schema(description = "배송지 주소")
    private String address;

    @Schema(description = "배송지 상세 주소")
    private String addressDetail;

    @Schema(description = "배송지 우편번호")
    private Long postcode;

    @Schema(description = "배송지 메모")
    private String description;

    // TODO: 결제정보 추가 필요

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "마지막 수정일시")
    private LocalDateTime updatedAt;

    @Schema(description = "삭제일시")
    private LocalDateTime deletedAt;

    public static OrderDetailResponse of(Order order, List<OrderItemResponse> orderItems) {
        return OrderDetailResponse.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .customerName(order.getMember().getName())
                .orderStatus(order.getOrderStatus())
                .orderItems(orderItems)
                .recipient(order.getRecipient())
                .phone(order.getPhone())
                .address(order.getAddress())
                .addressDetail(order.getDetail())
                .postcode(order.getPostcode())
                .description(order.getDescription())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .deletedAt(order.getDeletedAt())
                .build();
    }
}
