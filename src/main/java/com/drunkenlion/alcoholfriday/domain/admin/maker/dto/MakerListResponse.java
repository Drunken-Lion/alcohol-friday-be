package com.drunkenlion.alcoholfriday.domain.admin.maker.dto;

import com.drunkenlion.alcoholfriday.domain.maker.entity.Maker;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "전체 제조사 조회 항목")
public class MakerListResponse {
    @Schema(description = "고유 아이디")
    private Long id;

    @Schema(description = "이름")
    private String name;

    @Schema(description = "제조지역")
    private String region;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "삭제여부")
    private boolean deleted;

    public static MakerListResponse of(Maker maker) {
        return MakerListResponse.builder()
                .id(maker.getId())
                .name(maker.getName())
                .region(maker.getRegion())
                .createdAt(maker.getCreatedAt())
                .deleted(maker.getDeletedAt() != null)
                .build();
    }
}
