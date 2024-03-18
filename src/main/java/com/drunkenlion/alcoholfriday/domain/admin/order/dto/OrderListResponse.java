package com.drunkenlion.alcoholfriday.domain.admin.order.dto;

import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import com.drunkenlion.alcoholfriday.domain.payment.enumerated.PaymentCardCode;
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

    @Schema(description = "카드 발급사 이름")
    private String issuerName;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "삭제여부")
    private boolean deleted;

    public OrderListResponse(Order order, PaymentCardCode issuerCode) {
        this.id = order.getId();
        this.orderNo = order.getOrderNo();
        this.customerName = order.getMember().getName();
        this.customerNickname = order.getMember().getNickname();
        this.orderStatus = order.getOrderStatus();
        this.price = order.getPrice();
        this.issuerName =  PaymentCardCode.ofCardName(issuerCode);
        this.createdAt = order.getCreatedAt();
        this.deleted = order.getDeletedAt() != null;
    }
}
