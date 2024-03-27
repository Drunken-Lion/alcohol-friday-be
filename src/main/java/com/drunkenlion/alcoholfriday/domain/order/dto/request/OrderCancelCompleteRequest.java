package com.drunkenlion.alcoholfriday.domain.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Schema(description = "주문 취소 처리 요청 정보 (결제 취소)")
public class OrderCancelCompleteRequest {
    @Schema(description = "주문 고유 번호")
    private String orderNo;

    @Schema(description = "토스페이먼츠 결제 키")
    private String paymentKey;

    @Schema(description = "주문 취소 사유")
    private String cancelReason;
}
