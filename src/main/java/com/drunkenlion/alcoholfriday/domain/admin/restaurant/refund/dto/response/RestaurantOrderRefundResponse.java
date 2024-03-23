package com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dto.response;

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
@Schema(description = "매장 재고 환불 항목")
public class RestaurantOrderRefundResponse {
    @Schema(description = "환불 고유 아이디")
    private Long refundId;

    @Schema(description = "발주 고유 아이디")
    private Long orderId;

    @Schema(description = "발주 일자")
    private LocalDateTime orderCreatedAt;

    @Schema(description = "사업자명")
    private String businessName;

    @Schema(description = "주소")
    private String fullAddress;

    @Schema(description = "환불 사유")
    private String ownerReason;

    @Schema(description = "환불 일자")
    private LocalDateTime refundCreatedAt;

    @Schema(description = "환불 상태")
    private RestaurantOrderRefundStatus status;

    @Schema(description = "환불 요청 총 금액")
    private BigDecimal totalPrice;

    @Schema(description = "환불 제품 정보")
    private List<RestaurantOrderRefundDetailResponse> refundDetails;

    public static RestaurantOrderRefundResponse of(RestaurantOrderRefund refund, List<RestaurantOrderRefundDetailResponse> refundDetails) {
        return RestaurantOrderRefundResponse.builder()
                .refundId(refund.getId())
                .orderId(refund.getRestaurantOrder().getId())
                .orderCreatedAt(refund.getRestaurantOrder().getCreatedAt())
                .businessName(refund.getRestaurantOrder().getRestaurant().getBusinessName())
                .fullAddress(refund.getRestaurantOrder().getFullAddress())
                .ownerReason(refund.getOwnerReason())
                .refundCreatedAt(refund.getCreatedAt())
                .status(refund.getStatus())
                .totalPrice(refund.getTotalPrice())
                .refundDetails(refundDetails)
                .build();
    }
}
