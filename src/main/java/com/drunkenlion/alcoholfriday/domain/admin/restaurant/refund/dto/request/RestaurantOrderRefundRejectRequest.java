package com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "매장 재고 환불 반려 요청 항목")
public class RestaurantOrderRefundRejectRequest {
    @Schema(description = "환불 반려 사유")
    private String adminReason;
}
