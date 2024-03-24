package com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.response;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity.RestaurantOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "레스토랑 발주 리스트 응답 항목 (Owner)")
public class OwnerRestaurantOrderListResponse {
    @Schema(description = "발주 고유 아이디")
    private Long id;

    @Schema(description = "발주 상태")
    private String orderStatus;

    @Schema(description = "발주 일자")
    private LocalDateTime createdAt;

    @Schema(description = "사업자명")
    private String businessName;

    @Schema(description = "배송지 주소")
    private String address;

    @Schema(description = "배송지 상세 주소")
    private String addressDetail;

    @Schema(description = "우편번호")
    private String postcode;

    @Schema(description = "배송시 주의사항")
    private String description;

    @Schema(description = "발주 상품 리스트")
    private List<OwnerRestaurantOrderDetailResponse> orderDetails;

    public static OwnerRestaurantOrderListResponse of(
            RestaurantOrder restaurantOrder,
            List<OwnerRestaurantOrderDetailResponse> restaurantOrderDetailResponses) {

        return OwnerRestaurantOrderListResponse.builder()
                .id(restaurantOrder.getId())
                .orderStatus(restaurantOrder.getOrderStatus().getName())
                .createdAt(restaurantOrder.getCreatedAt())
                .businessName(restaurantOrder.getRestaurant().getBusinessName())
                .address(restaurantOrder.getAddress())
                .addressDetail(restaurantOrder.getAddressDetail())
                .postcode(restaurantOrder.getPostcode())
                .description(restaurantOrder.getDescription())
                .orderDetails(restaurantOrderDetailResponses)
                .build();
    }
}
