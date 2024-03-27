package com.drunkenlion.alcoholfriday.domain.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Schema(description = "주문 취소 요청 정보")
public class OrderCancelRequest {
    @Schema(description = "주문 취소 사유")
    private String cancelReason;
}
