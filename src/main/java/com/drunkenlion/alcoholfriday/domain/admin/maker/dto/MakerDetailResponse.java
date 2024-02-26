package com.drunkenlion.alcoholfriday.domain.admin.maker.dto;

import com.drunkenlion.alcoholfriday.domain.maker.entity.Maker;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "제조사 상세 조회 항목")
public class MakerDetailResponse {
    @Schema(description = "고유 아이디")
    private Long id;

    @Schema(description = "이름")
    private String name;

    @Schema(description = "주소")
    private String address;

    @Schema(description = "상세주소")
    private String detail;

    @Schema(description = "제조지역")
    private String region;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "마지막 수정일시")
    private LocalDateTime updatedAt;

    @Schema(description = "삭제일시")
    private LocalDateTime deletedAt;

    public static MakerDetailResponse of(Maker maker) {
        return MakerDetailResponse.builder()
                .id(maker.getId())
                .name(maker.getName())
                .address(maker.getAddress())
                .detail(maker.getDetail())
                .region(maker.getRegion())
                .createdAt(maker.getCreatedAt())
                .updatedAt(maker.getUpdatedAt())
                .deletedAt(maker.getDeletedAt())
                .build();
    }
}
