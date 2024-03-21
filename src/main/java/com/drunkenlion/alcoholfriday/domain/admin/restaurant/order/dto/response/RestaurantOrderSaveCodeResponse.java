package com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.response;

import com.drunkenlion.alcoholfriday.domain.restaurant.order.entity.RestaurantOrder;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "레스토랑 발주 저장 코드 반환 객체")
public class RestaurantOrderSaveCodeResponse {
    @Schema(description = "레스토랑 발주 고육 식별 ID")
    private Long id;

    @Schema(description = "레스토랑 사업자 이름")
    private String businessName;

    @Schema(description = "발주 주소")
    private String address;

    @Schema(description = "발주 총 금액")
    private BigDecimal totalPrice;

    @Schema(description = "레스토랑 주문자 정보")
    private RestaurantOrderMemberResponse member;

    @Schema(description = "발주 제품 목록")
    private List<RestaurantOrderDetailResponse> details;

    public static RestaurantOrderSaveCodeResponse of(RestaurantOrder rs, List<RestaurantOrderDetailResponse> details) {
        return RestaurantOrderSaveCodeResponse.builder()
                .id(rs.getId())
                .businessName(rs.getRestaurant().getBusinessName())
                .address("%s %s [%s]".formatted(rs.getAddress(), rs.getAddressDetail(), rs.getPostcode()))
                .totalPrice(rs.getTotalPrice())
                .member(RestaurantOrderMemberResponse.of(rs.getMember()))
                .details(details)
                .build();
    }
}
