package com.drunkenlion.alcoholfriday.domain.admin.order.dto;

import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import com.drunkenlion.alcoholfriday.global.common.enumerated.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "전체 주문 조회 항목")
public class OrderListResponse {
    @Schema(description = "고유 아이디")
    private Long id;

    @Schema(description = "주문번호")
    private String orderNo;

    @Schema(description = "주문자 이름")
    private String customerName;

    @Schema(description = "주문자 별명")
    private String customerNickname;

    @Schema(description = "주문 상태")
    private OrderStatus orderStatus;

    @Schema(description = "주문 총 금액")
    private BigDecimal price;

    // TODO: 결제 플랫폼 추가 필요
//    @Schema(description = "결제 플랫폼")
//    private String platform;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "삭제여부")
    private boolean deleted;

    public static OrderListResponse of(Order order) {
        return OrderListResponse.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .customerName(order.getMember().getName())
                .customerNickname(order.getMember().getNickname())
                .orderStatus(order.getOrderStatus())
                .price(order.getPrice())
                .createdAt(order.getCreatedAt())
                .deleted(order.getDeletedAt() != null)
                .build();
    }
}
