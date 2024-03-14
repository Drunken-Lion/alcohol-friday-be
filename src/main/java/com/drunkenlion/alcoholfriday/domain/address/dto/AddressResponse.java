package com.drunkenlion.alcoholfriday.domain.address.dto;

import com.drunkenlion.alcoholfriday.domain.address.entity.Address;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "배송지 응답 항목")
public class AddressResponse {
    @Schema(description = "배송지 고유 아이디")
    private Long id;

    @Schema(description = "대표 주소 여부")
    private Boolean isPrimary;

    @Schema(description = "주소")
    private String address;

    @Schema(description = "상세 주소")
    private String addressDetail;

    @Schema(description = "우편번호")
    private String postcode;

    @Schema(description = "받는 사람")
    private String recipient;

    @Schema(description = "받는 사람 연락처")
    private Long phone;

    @Schema(description = "배송시 요청사항")
    private String request;

    public static AddressResponse of(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .recipient(address.getRecipient())
                .phone(address.getPhone())
                .request(address.getRequest())
                .address(address.getAddress())
                .addressDetail(address.getAddressDetail())
                .postcode(address.getPostcode())
                .isPrimary(address.getIsPrimary())
                .build();
    }
}
