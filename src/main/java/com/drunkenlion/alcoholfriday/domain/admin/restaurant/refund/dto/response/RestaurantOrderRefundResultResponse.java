package com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dto.response;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.entity.RestaurantOrderRefund;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.enumerated.RestaurantOrderRefundStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "매장 발주 환불 반환 객체")
public class RestaurantOrderRefundResultResponse {
    @Schema(description = "매장 발주 환불 고유 아이디")
    private Long id;

    @Schema(description = "환불 총 가격")
    private BigDecimal totalPrice;

    @Schema(description = "사장 환불 사유")
    private String ownerReason;

    @Schema(description = "관리자 반려 사유")
    private String adminReason;

    @Schema(description = "환불 상태 정보")
    private RestaurantOrderRefundStatus status;

    public static RestaurantOrderRefundResultResponse of(RestaurantOrderRefund refund) {
        return RestaurantOrderRefundResultResponse.builder()
                .id(refund.getId())
                .totalPrice(refund.getTotalPrice())
                .ownerReason(refund.getOwnerReason())
                .adminReason(refund.getAdminReason())
                .status(refund.getStatus())
                .build();
    }
}
