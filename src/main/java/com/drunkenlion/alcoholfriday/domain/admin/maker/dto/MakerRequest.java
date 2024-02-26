package com.drunkenlion.alcoholfriday.domain.admin.maker.dto;

import com.drunkenlion.alcoholfriday.domain.maker.entity.Maker;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "제조사 입력 요청 항목")
public class MakerRequest {
    @Schema(description = "이름")
    private String name;

    @Schema(description = "주소")
    private String address;

    @Schema(description = "상세주소")
    private String detail;

    @Schema(description = "제조지역")
    private String region;

    public static Maker toEntity(MakerRequest request) {
        return Maker.builder()
                .name(request.getName())
                .address(request.getAddress())
                .detail(request.getDetail())
                .region(request.getRegion())
                .build();
    }
}
