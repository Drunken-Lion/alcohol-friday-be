package com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "매장 정보 요청 항목")
public class RestaurantInfoRequest {
    @Schema(description = "매장 고유 아이디")
    private Long restaurantId;
}
