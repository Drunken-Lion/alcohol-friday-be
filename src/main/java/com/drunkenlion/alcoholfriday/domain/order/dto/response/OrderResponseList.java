package com.drunkenlion.alcoholfriday.domain.order.dto.response;

import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import com.drunkenlion.alcoholfriday.domain.order.entity.OrderDetail;
import com.drunkenlion.alcoholfriday.global.common.enumerated.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Schema(description = "주문 접수한 내역")
public class OrderResponseList {
    @Schema(description = "주문 테이블 번호")
    private Long id;

    @Schema(description = "주문 고유번호")
    private String orderNo;

    @Schema(description = "주문 상태정보")
    private OrderStatus orderStatus;

    @Schema(description = "주문 날짜")
    private LocalDateTime createdAt;

    @Schema(description = "주문 총 금액")
    private BigDecimal totalPrice;

    @Schema(description = "주문 상품 총 수량")
    private Long totalQuantity;

    @Schema(description = "배송받는 사람")
    private String recipient;

    @Schema(description = "배송받는 사람의 연락처")
    private Long phone;

    @Schema(description = "배송지 주소")
    private String address;

    @Schema(description = "배송지 상세 주소")
    private String detail;

    @Schema(description = "배송시 주의사항")
    private String description;

    @Schema(description = "배송지 우편번호")
    private Long postcode;

    @Schema(description = "상품 리스트")
    private List<OrderDetailResponse> itemList;

    public static OrderResponseList of(Order order, List<OrderDetail> orderDetailList) {
        List<OrderDetailResponse> itemList = orderDetailList.stream()
                .map(OrderDetailResponse::of)
                .toList();

        return OrderResponseList.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .orderStatus(order.getOrderStatus())
                .createdAt(order.getCreatedAt())
                .totalPrice(order.getPrice())
                .totalQuantity(order.getTotalOrderQuantity(orderDetailList))
                .recipient(order.getRecipient())
                .phone(order.getPhone())
                .address(order.getAddress())
                .detail(order.getDetail())
                .description(order.getDescription())
                .postcode(order.getPostcode())
                .itemList(itemList)
                .build();

    }
}
