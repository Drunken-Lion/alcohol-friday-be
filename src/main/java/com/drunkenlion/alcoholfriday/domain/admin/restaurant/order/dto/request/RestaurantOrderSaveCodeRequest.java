package com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "레스토랑 발주 저장 코드 요청 객체")
public class RestaurantOrderSaveCodeRequest {
    @Schema(description = "제품 고유 식별 ID")
    private Long restaurantId;
}


