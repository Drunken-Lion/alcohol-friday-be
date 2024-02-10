package com.drunkenlion.alcoholfriday.domain.admin.restaurant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.geo.Point;

import java.util.Map;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "매장 등록 요청 항목")
public class RestaurantCreateRequest {
    @Schema(description = "매장 사장의 고유 아이디")
    private Long memberId;

    @Schema(description = "매장 이름")
    private String name;

    @Schema(description = "매장 카테고리")
    private String category;

    @Schema(description = "매장 주소")
    private String address;

    @Schema(description = "매장 위치 (위도, 경도)")
    private Point location;

    @Schema(description = "매장 연락처")
    private Long contact;

    @Schema(description = "메뉴 목록")
    private Map<String, Object> menu;

    @Schema(description = "영업시간")
    private Map<String, Object> time;

    @Schema(description = "편의시설 목록")
    private Map<String, Object> provision;
}
