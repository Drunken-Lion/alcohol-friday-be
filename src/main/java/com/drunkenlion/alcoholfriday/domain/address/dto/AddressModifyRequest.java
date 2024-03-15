package com.drunkenlion.alcoholfriday.domain.address.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "배송지 수정 요청 항목")
public class AddressModifyRequest {
    @Schema(description = "받는 사람")
    private String recipient;

    @Schema(description = "주소")
    private String address;

    @Schema(description = "상세 주소")
    private String addressDetail;

    @Schema(description = "우편 번호")
    private String postcode;

    @Schema(description = "받는 사람 연락처")
    private Long phone;

    @Schema(description = "배송 시 요청 사항")
    private String request;

    @Schema(description = "대표 주소 여부")
    private Boolean isPrimary;
}
