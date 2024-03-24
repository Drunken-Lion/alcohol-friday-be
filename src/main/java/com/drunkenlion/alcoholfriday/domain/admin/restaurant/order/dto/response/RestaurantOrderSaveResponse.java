package com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.response;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity.RestaurantOrder;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.enumerated.RestaurantOrderStatus;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.util.RestaurantOrderAddressConvertor;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "레스토랑 발주 저장 반환 객체")
public class RestaurantOrderSaveResponse {
    @Schema(description = "레스토랑 발주 고유 식별 ID")
    private Long id;

    @Schema(description = "레스토랑 사업자 이름")
    private String businessName;

    @Schema(description = "발주 주소")
    private String address;

    @Schema(description = "배송 주의사항")
    private String description;

    @Schema(description = "발주 총 금액")
    private BigDecimal totalPrice;

    @Schema(description = "레스토랑 주문자 정보")
    private RestaurantOrderMemberResponse member;

    @Schema(description = "발주 상태")
    private RestaurantOrderStatus status;

    @Schema(description = "발주 제품 목록")
    private List<RestaurantOrderDetailResponse> details;

    public static RestaurantOrderSaveResponse of(RestaurantOrder rs, List<RestaurantOrderDetailResponse> details) {
        return RestaurantOrderSaveResponse.builder()
                .id(rs.getId())
                .businessName(rs.getRestaurant().getBusinessName())
                .address(RestaurantOrderAddressConvertor.addressFormatter(rs.getRestaurant()))
                .description(rs.getDescription())
                .totalPrice(rs.getTotalPrice())
                .status(rs.getOrderStatus())
                .member(RestaurantOrderMemberResponse.of(rs.getMember()))
                .details(details)
                .build();
    }
}
