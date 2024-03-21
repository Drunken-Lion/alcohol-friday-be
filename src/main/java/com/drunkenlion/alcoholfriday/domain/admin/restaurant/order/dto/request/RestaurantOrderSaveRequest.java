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
@Schema(description = "레스토랑 발주 요청 객체")
public class RestaurantOrderSaveRequest {
    @Schema(description = "배송 주의 사항")
    private String description;

    @Schema(description = "연락처")
    private Long phone;

    @Schema(description = "받는 사람 이름")
    private String recipient;
}
