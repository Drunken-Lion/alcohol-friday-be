package com.drunkenlion.alcoholfriday.domain.address.dto;

import com.drunkenlion.alcoholfriday.domain.address.entity.Address;
import com.drunkenlion.alcoholfriday.domain.member.dto.MemberResponse;
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

    @Schema(description = "해당 배송지를 가진 회원 정보")
    private MemberResponse member;

    @Schema(description = "대표 주소 여부")
    private Boolean isPrimary;

    @Schema(description = "주소")
    private String address;

    @Schema(description = "상세 주소")
    private String addressDetail;

    @Schema(description = "우편번호")
    private Long postcode;

    public static AddressResponse of(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .member(MemberResponse.of(address.getMember()))
                .isPrimary(address.getIsPrimary())
                .address(address.getAddress())
                .addressDetail(address.getDetail())
                .postcode(address.getPostcode())
                .build();
    }
}
