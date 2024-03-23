package com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dto.response;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.entity.RestaurantOrderRefund;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.enumerated.RestaurantOrderRefundStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "매장 발주 취소 반환 객체")
public class RestaurantOwnerOrderRefundCancelResponse {
    @Schema(description = "매장 발주 환불 고유 아이디")
    private Long id;

    @Schema(description = "사장 환불 사유")
    private String ownerReason;

    @Schema(description = "환불 상태 정보")
    private RestaurantOrderRefundStatus status;

    public static RestaurantOwnerOrderRefundCancelResponse of(RestaurantOrderRefund refund) {
        return RestaurantOwnerOrderRefundCancelResponse.builder()
                .id(refund.getId())
                .ownerReason(refund.getOwnerReason())
                .status(refund.getStatus())
                .build();
    }
}
