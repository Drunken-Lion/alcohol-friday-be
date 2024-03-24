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
@Schema(description = "레스토랑 발주 리스트 응답 항목 (Admin)")
public class RestaurantOrderListResponse {
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

    @Schema(description = "발주 제품 목록")
    private List<RestaurantOrderDetailResponse> details;

    public static RestaurantOrderListResponse of(RestaurantOrder order, List<RestaurantOrderDetailResponse> detailResponses) {
        return RestaurantOrderListResponse.builder()
                .id(order.getId())
                .orderStatus(order.getOrderStatus().getName())
                .createdAt(order.getCreatedAt())
                .businessName(order.getRestaurant().getBusinessName())
                .address(order.getAddress())
                .addressDetail(order.getAddressDetail())
                .postcode(order.getPostcode())
                .description(order.getDescription())
                .details(detailResponses)
                .build();
    }
}
