package com.drunkenlion.alcoholfriday.domain.address.dto;

import com.drunkenlion.alcoholfriday.domain.address.entity.Address;
import com.drunkenlion.alcoholfriday.domain.member.dto.MemberResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "")
public class AddressResponse {
    private Long id;
    private MemberResponse member;
    private Boolean isPrimary;
    private String address;
    private String addressDetail;
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
