package com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.response;

import com.drunkenlion.alcoholfriday.domain.restaurant.order.entity.RestaurantOrder;
import com.drunkenlion.alcoholfriday.domain.restaurant.order.enumerated.RestaurantOrderStatus;
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
@Schema(description = "관리자 레스토랑 발주 승인 반환 객체")
public class RestaurantAdminOrderApprovalResponse {
    @Schema(description = "레스토랑 발주 고유 식별 ID")
    private Long id;

    @Schema(description = "레스토랑 사업자 이름")
    private String businessName;

    @Schema(description = "발주 상태")
    private RestaurantOrderStatus status;

    public static RestaurantAdminOrderApprovalResponse of(RestaurantOrder order) {
        return RestaurantAdminOrderApprovalResponse.builder()
                .id(order.getId())
                .businessName(order.getRestaurant().getBusinessName())
                .status(order.getOrderStatus())
                .build();
    }
}
