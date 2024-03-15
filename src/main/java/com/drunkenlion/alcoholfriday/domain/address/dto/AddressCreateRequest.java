package com.drunkenlion.alcoholfriday.domain.address.dto;

import com.drunkenlion.alcoholfriday.domain.address.entity.Address;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "배송지 등록 정보")
public class AddressCreateRequest {
    @Schema(description = "받는 사람")
    private String recipient;

    @Schema(description = "연락처")
    private Long phone;

    @Schema(description = "배송시 요청사항")
    private String request;

    @Schema(description = "주소")
    private String address;

    @Schema(description = "상세 주소")
    private String detail;

    @Schema(description = "우편 번호")
    private String postcode;

    @Schema(description = "대표 주소 여부")
    private Boolean isPrimary;

    public static Address toEntity(AddressCreateRequest request, Member member) {
        return Address.builder()
                .recipient(request.getRecipient())
                .phone(request.getPhone())
                .request(request.getRequest())
                .address(request.getAddress())
                .addressDetail(request.getDetail())
                .postcode(request.getPostcode())
                .isPrimary(request.getIsPrimary())
                .member(member)
                .build();
    }
}
