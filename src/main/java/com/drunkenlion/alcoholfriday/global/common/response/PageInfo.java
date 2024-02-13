package com.drunkenlion.alcoholfriday.global.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.domain.Page;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Schema(description = "페이징 객체")
public class PageInfo {
    @Schema(description = "요청된 row size")
    private int size;

    @Schema(description = "응답된 총 row 갯수")
    private Long count;

    public static PageInfo of(Page page) {
        return PageInfo.builder()
                .size(page.getPageable().getPageSize())
                .count(page.getTotalElements())
                .build();
    }
}
