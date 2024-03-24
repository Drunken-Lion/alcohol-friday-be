package com.drunkenlion.alcoholfriday.domain.order.dto.response;

import com.drunkenlion.alcoholfriday.domain.address.dto.AddressResponse;
import com.drunkenlion.alcoholfriday.domain.member.dto.MemberResponse;
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

    @Schema(description = "주문 상품 총 금액")
    private BigDecimal price;

    @Schema(description = "배송 금액")
    private BigDecimal deliveryPrice;

    @Schema(description = "배송비 포함 주문 총 금액")
    private BigDecimal totalPrice;

    @Schema(description = "주문 상품 총 수량")
    private Long totalQuantity;

    @Schema(description = "배송 정보")
    private AddressResponse addressInfo;

    @Schema(description = "구매자 정보")
    private MemberResponse memberInfo;

    @Schema(description = "상품 리스트")
    private List<OrderDetailResponse> orderDetails;

    public static OrderResponseList of(
            Order order,
            List<OrderDetail> orderDetailList,
            AddressResponse addressInfo,
            MemberResponse memberInfo) {
        List<OrderDetailResponse> itemList = orderDetailList.stream()
                .map(OrderDetailResponse::of)
                .toList();

        return OrderResponseList.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .orderStatus(order.getOrderStatus())
                .createdAt(order.getCreatedAt())
                .price(order.getPrice())
                .deliveryPrice(order.getDeliveryPrice())
                .totalPrice(order.getTotalPrice())
                .totalQuantity(order.getTotalOrderQuantity(orderDetailList))
                .addressInfo(addressInfo)
                .memberInfo(memberInfo)
                .orderDetails(itemList)
                .build();
    }
}
