package com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dto.request;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity.RestaurantOrder;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.enumerated.RestaurantOrderStatus;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.entity.RestaurantOrderRefund;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.enumerated.RestaurantOrderRefundStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "매장 재고 환불 등록 요청 항목")
public class RestaurantOrderRefundCreateRequest {
    @Schema(description = "매장 고유 아이디")
    private Long restaurantId;

    @Schema(description = "매장 주문 고유 아이디")
    private Long orderId;

    @Schema(description = "발주 일자")
    private LocalDateTime orderDate;

    @Schema(description = "발주 상태")
    private RestaurantOrderStatus status;

    @Schema(description = "사장 환불 사유")
    private String ownerReason;

    @Schema(description = "환불 제품 정보")
    private List<RestaurantOrderRefundDetailCreateRequest> refundDetails;

    public static RestaurantOrderRefund toEntity(
            RestaurantOrderRefundCreateRequest request,
            RestaurantOrder restaurantOrder,
            BigDecimal totalPrice
    ) {
        return RestaurantOrderRefund.builder()
                .restaurant(restaurantOrder.getRestaurant())
                .restaurantOrder(restaurantOrder)
                .totalPrice(totalPrice)
                .ownerReason(request.getOwnerReason())
                .status(RestaurantOrderRefundStatus.WAITING_APPROVAL)
                .build();
    }
}
